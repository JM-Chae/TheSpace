package com.thespace.spaceweb.friendship;

import com.thespace.common.exception.NotFoundFriendship;
import com.thespace.common.exception.NotOwnerFriendship;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserService;
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

    @Transactional
    public void request(User fromUser, String toUserUuid) {

        User toUser = userService.findUserByUuid(toUserUuid);

        Optional<Friendship> reverseRequest = friendshipRepository.findByFromAndTo(toUser, fromUser);

        if (reverseRequest.isPresent()) {

            Friendship reverseFriendship  = reverseRequest.get();

            if (reverseFriendship.getStatus() == Status.BLOCKED) return;

            reverseFriendship.changeStatus(Status.ACCEPTED);
            friendshipRepository.save(new Friendship(reverseFriendship.getTo(), reverseFriendship.getFrom(), Status.ACCEPTED));

            return;
        }

        friendshipRepository.save(new Friendship(fromUser, toUser, Status.REQUEST));
    }

    @Transactional
    public void accept(User user, Long fid) {

        Friendship friendship = friendshipRepository.findById(fid).orElseThrow(NotFoundFriendship::new);
        if (!Objects.equals(friendship.getTo(), user)) throw new NotOwnerFriendship();

        friendship.changeStatus(Status.ACCEPTED);

        friendshipRepository.save(friendship);
        friendshipRepository.save(new Friendship(friendship.getTo(), friendship.getFrom(), Status.ACCEPTED));
    }

    @Transactional
    public void block(User fromUser, String toUserUuid) {

        User toUser = userService.findUserByUuid(toUserUuid);

        friendshipRepository.findByFromAndTo(toUser, fromUser).ifPresent(friendshipRepository::delete);

        friendshipRepository.findByFromAndTo(fromUser, toUser).ifPresentOrElse(f -> f.changeStatus(Status.BLOCKED),
                () -> friendshipRepository.save(new Friendship(fromUser, toUser, Status.BLOCKED)));
    }

    @Transactional
    public void memo(User user, Long fid, String memo) {

        Friendship friendship = friendshipRepository.findById(fid).orElseThrow(NotFoundFriendship::new);
        if (!Objects.equals(friendship.getFrom(), user)) throw new NotOwnerFriendship();

        friendship.changeMemo(memo);

        friendshipRepository.save(friendship);
    }
}
