package com.thespace.thespace.service;


import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.CommunityDTO;
import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;
import com.thespace.thespace.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class CommunityServiceImpl implements CommunityService
  {
    private CommunityRepository communityRepository;
    private ModelMapper modelMapper;

    @Autowired
    public void setCommunityRepository(CommunityRepository communityRepository, ModelMapper modelMapper)
      {
        this.communityRepository=communityRepository;
        this.modelMapper=modelMapper;
      }

    public Long createCommunity(CommunityDTO communityDTO)
      {
        Community community= modelMapper.map(communityDTO, Community.class);

        Long communityId = communityRepository.save(community).getCommunityId();
        return communityId;
      }

    @Override
    public boolean check(String communityName)
      {
        return !communityRepository.existsByCommunityName(communityName);
      }

    @Override
    public PageResDTO<CommunityDTO> getCommunityList(PageReqDTO pageReqDTO)
      {
        String[] types = pageReqDTO.getTypes();
        String keyword = pageReqDTO.getKeyword();

        Pageable pageable = pageReqDTO.getPageable("communityId");

        Page<CommunityDTO> list = communityRepository.getList(types, keyword, pageable);

        PageResDTO<CommunityDTO> pageResDTO = PageResDTO.<CommunityDTO>PageResDTO()
            .pageReqDTO(pageReqDTO)
            .dtoList(list.getContent())
            .total((int) list.getTotalElements())
            .build();

        return pageResDTO;
      }

    public void deleteCommunity(Long communityId)
      {
        communityRepository.deleteById(communityId);
      }

    @Override
    public CommunityDTO getCommunity(String communityName)
      {
        Community community = communityRepository.findByCommunityName(communityName);
        return modelMapper.map(community, CommunityDTO.class);
      }

    public Long getCommunityIdByName(String communityName)
      {
        return communityRepository.findCommunityIdByNameIgnoreCase(communityName);
      }
  }
