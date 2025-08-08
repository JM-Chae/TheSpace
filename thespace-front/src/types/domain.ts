export interface CommunityInfo {
  id: number;
  name: string;
  createdAt: string;
  modifiedAt: string;
  description: string;
}

export interface CategoryInfo {
  id: number;
  name: string;
  type: string;
  createdAt: string;
  modifiedAt: string;
  communityId: number;
}

export interface friendshipInfo {
  fid: number,
  status: string,
  memo: string
  acceptedAt: string
}