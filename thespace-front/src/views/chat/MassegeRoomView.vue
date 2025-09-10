<script lang="ts" setup>


</script>
<template>
  <!-- pop-up list -->
  <transition name="pop">
    <div style="z-index: 9999; display: flex; flex-direction: column; position: fixed; bottom: 1em; right: 20px; width: 25em; height: 80vh; border-radius: 0.5em; box-shadow: 0.1em 0.1em 1em 0.1em #878787; padding: 1em; margin: 0.5em 0 0.5em 0; background-color: rgb(18,18,18);">
      <div style="display: flex; justify-content: center; border-bottom: 1px solid #434343; padding-bottom: 0.4em">
        <el-text style="font-size: 1.3em; font-weight: bold; color: #00DE64;"><!-- {{Chatroom.name}} --></el-text>
        <!-- Popup close button -->
        <el-button link style="position: absolute; right: 1em; top: 1.3em;" @click="">X</el-button>
      </div>
      <div class="scroll">
        <ul v-if="friendsList" class="friends" @contextmenu.prevent>
          <li v-for="(friend) in friendsList.dtoList" :key="friend.fid" class="friends-item" @dblclick="routToFriendMypage(friend)">
            <div class="fList" @mouseleave="offInfoHover()" @mousemove="handleInfoHover(friend, $event)" @contextmenu.prevent="handleContextmenu(friend, $event)">
              <el-text class="signature">{{friend.fsignature}}</el-text>
              <div class="fInfo">
                <el-text class="fContent name" >{{friend.fname}}#{{friend.fuuid}}</el-text>
                <el-text class="fContent intro" line-clamp="4">{{friend.fintro}}</el-text>
              </div>
            </div>
          </li>
        </ul>
        <infinite-loading v-if="showFriendList" @infinite="infiniteHandler">
          <template #complete><span> </span></template>
        </infinite-loading>
      </div>
    </div>
  </transition>

  <div v-if="menuVisible" ref="menuRef" :style="{ top: menuPosition.top, left: menuPosition.left }" class="context-menu">
    <div class="context-menu-item" @click="handleMenuClick('profile')">Move to Profile</div>
    <div class="context-menu-item" @click="handleMenuClick('message')">Send message</div>
    <div class="context-menu-item" @click="handleMenuClick('note')">Memo</div>
    <div class="context-menu-item" @click="handleMenuClick('block')">Block</div>
  </div>

  <div v-if="info_hover && hoveredFriend" ref="infoHoverRef" :style="infoHoverPosition" class="info-hover" @mouseenter="clearHideTimeout" @mouseleave="offInfoHover">
    <el-text class="fContent note">Note: {{hoveredFriend?.note}}</el-text>
    <el-text class="fContent date">Since, {{formatDate(hoveredFriend?.acceptedAt)}}</el-text>
  </div>

</template>

<style scoped>

</style>