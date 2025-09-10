export interface CommunityInfo {
  id: number;
  name: string;
  createdAt: string;
  modifiedAt: string;
  description: string;
}

export interface User {
  signature: string,
  name: string,
  uuid: string,
  introduce: string,
  joinedOn: string
}

export interface CategoryInfo {
  id: number;
  name: string;
  type: string;
  createdAt: string;
  modifiedAt: string;
  communityId: number;
}

export interface FriendshipInfo {
  fid: number,
  status: string,
  memo: string
  acceptedAt: string
}

export interface Board {
  bno: number,
  title: string,
  content: string,
  communityInfo: CommunityInfo,
  writer: string,
  writerUuid: string,
  createDate: string,
  modDate: string,
  viewCount: number,
  vote: number,
  fileNames: string[],
  thumbCheck: boolean,
  categoryInfo: CategoryInfo,
  rCount: number
}

export interface Notification {
  nno: number,
  title: string,
  body: string,
  url: string,
  isRead: boolean,
  dataPayload: {
    fid: number,
    targetNameAndUUID: string
    status: string
  },
  type: string,
  sentAt: string
}

export interface ListRes<T> {
  page: number,
  size: number,
  total: number,
  dtoList: T[]
}

export interface Friend {
  fid: number,
  fsignature: string,
  fname: string,
  fuuid: string,
  fintro: string,
  note: string,
  acceptedAt: string
}
// Long roomId, String name, UserDTOs.Info manager, String description, List<UserDTOs.Info> members,
// LocalDateTime createdAt, LocalDateTime modifiedAt
export interface ChatRoom {

}