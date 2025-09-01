package com.thespace.spaceweb.friendship;

import com.thespace.common.exception.Exceptions.NotFoundFriendship;
import com.thespace.common.exception.Exceptions.NotOwnerFriendship;
import com.thespace.common.notification.FriendshipNotificationStatus;
import com.thespace.common.notification.NotificationDTOs.ToFriendship_accept;
import com.thespace.common.notification.NotificationDTOs.ToFriendship_request;
import com.thespace.common.notification.NotificationService;
import com.thespace.spaceweb.friendship.FriendshipDTOs.Info;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserService;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    void acceptFriendship(Friendship friendship) {
        friendship.changeStatus(Status.ACCEPTED);
        friendship.acceptedAtWriter();
        friendshipRepository.save(friendship);

        Friendship reverseFriendship = new Friendship(friendship.getTo(), friendship.getFrom(), Status.ACCEPTED);
        reverseFriendship.acceptedAtWriter();
        friendshipRepository.save(reverseFriendship);

        String fromNameAndUUID = friendship.getFrom().getName() + "#" + friendship.getFrom().getUuid();
        String toNameAndUUID = friendship.getTo().getName() + "#" + friendship.getTo().getUuid();

        notificationService.sendNotification(new ToFriendship_accept(fromNameAndUUID, friendship.getTo().getId()));
        notificationService.sendNotification(new ToFriendship_accept(toNameAndUUID, friendship.getFrom().getId()));

        notificationService.updateFriendshipRequestNotification(friendship.getFid(), fromNameAndUUID, FriendshipNotificationStatus.ACCEPTED);
    }

    @Transactional
    public void request(User fromUser, String toUserUuid) {

        User toUser = userService.findUserByUuid(toUserUuid);

        Optional<Friendship> reverseRequest = findFriendshipByFromAndTo(toUser, fromUser);

        if (reverseRequest.isPresent()) {

            Friendship reverseFriendship  = reverseRequest.get();

            if (reverseFriendship.getStatus() == Status.BLOCKED) return;

            acceptFriendship(reverseFriendship);
            return;
        }

        Long fid = friendshipRepository.save(new Friendship(fromUser, toUser, Status.REQUEST)).getFid();
        notificationService.sendNotification(new ToFriendship_request(fid,
            fromUser.getName() + "#" + fromUser.getUuid(),
            toUser.getId()));
    }

    //by fromUser
    @Transactional
    public void deleteRequest(User user, Long fid) {

        Friendship request = friendshipRepository.findById(fid).orElseThrow(NotFoundFriendship::new);

        if (!request.getStatus().equals(Status.REQUEST) || !request.getFrom().equals(user))
            throw new NotOwnerFriendship();

        friendshipRepository.delete(request);
        notificationService.updateFriendshipRequestNotification(fid, null, FriendshipNotificationStatus.CANCELLED);
    }

    //by toUser
    @Transactional
    public void reject(User user, Long fid) {

        Friendship friendship = friendshipRepository.findById(fid).orElseThrow(NotFoundFriendship::new);
        if (!Objects.equals(friendship.getTo(), user)) throw new NotOwnerFriendship();

        String fromNameAndUUID = friendship.getFrom().getName() + "#" + friendship.getFrom().getUuid();

        notificationService.updateFriendshipRequestNotification(friendship.getFid(), fromNameAndUUID,FriendshipNotificationStatus.REJECTED);
        friendshipRepository.delete(friendship);
    }

    @Transactional
    public void accept(User user, Long fid) {

        Friendship friendship = friendshipRepository.findById(fid).orElseThrow(NotFoundFriendship::new);
        if (!Objects.equals(friendship.getTo(), user)) throw new NotOwnerFriendship();

        acceptFriendship(friendship);
    }

    @Transactional
    public void block(User fromUser, String toUserUuid) {

        User toUser = userService.findUserByUuid(toUserUuid);

        findFriendshipByFromAndTo(toUser, fromUser).ifPresent(friendshipRepository::delete);

        findFriendshipByFromAndTo(fromUser, toUser).ifPresentOrElse(f -> f.changeStatus(Status.BLOCKED),
                () -> friendshipRepository.save(new Friendship(fromUser, toUser, Status.BLOCKED)));
    }

    @Transactional
    public void unblock(User user, Long fid) {

        Friendship friendship = friendshipRepository.findById(fid).orElseThrow(NotFoundFriendship::new);

        if (!friendship.getStatus().equals(Status.BLOCKED) || !friendship.getFrom().equals(user))
            throw new NotOwnerFriendship();

        friendshipRepository.delete(friendship);
    }

    @Transactional
    public void memo(User user, Long fid, String memo) {

        Friendship friendship = friendshipRepository.findById(fid).orElseThrow(NotFoundFriendship::new);
        if (!Objects.equals(friendship.getFrom(), user)) throw new NotOwnerFriendship();

        friendship.changeMemo(memo);

        friendshipRepository.save(friendship);
    }

    public Info getInfo(User fromUser, String toUserUuid) {
        User toUser = userService.findUserByUuid(toUserUuid);

        return findFriendshipByFromAndTo(fromUser, toUser)
            .map(friendship -> new Info(
                friendship.getFid(),
                friendship.getStatus().name(),
                friendship.getMemo(),
                friendship.getAcceptedAt()))
            .orElse(new Info(0L, Status.NONE.name(), "", LocalDateTime.MIN));
    }

    Optional<Friendship> findFriendshipByFromAndTo(User fromUser, User toUser) {
        return friendshipRepository.findByFromAndTo(fromUser, toUser);
    }
}
