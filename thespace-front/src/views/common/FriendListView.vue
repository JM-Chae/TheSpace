<script lang="ts" setup>
import {nextTick, ref, watch} from "vue";
import {
  formatDate,
  friendshipBlock,
  friendshipNote,
  routToFriendMypage,
  useFriendsStore
} from "@/stores/friendship";
import type {Friend} from "@/types/domain";
import {ElMessage} from "element-plus";
import {storeToRefs} from "pinia";

const {friendsList, showFriendList} = storeToRefs(useFriendsStore());
const infoHoverRef = ref<HTMLElement | null>(null);

const page = ref(2);
const size = ref(10);
const keyword = ref("")

const infiniteHandler = async ($state: any) => {

  if (friendsList.value && friendsList.value.dtoList.length >= friendsList.value.total) {
    $state.complete();
    return
  }
  const MAX_PAGES = 1000;
  if (page.value > MAX_PAGES) {
    console.warn("Infinite loading stopped: Maximum page limit (${MAX_PAGES}) reached.");
    $state.complete();
    return;
  }
  try {
      const loaded = await useFriendsStore().updateFriendsList();
    if (loaded == 0) {
      $state.complete();
    } else {
      $state.loaded();
    }
    } catch (e) {
    console.error(e);
    $state.error();
  }
}

const menuPosition = ref({top: '0px', left: '0px'});
const infoHoverPosition = ref({top: '0px', left: '0px'});
const menuVisible = ref(false);
const selectedFriend = ref<Friend>();
const hoveredFriend = ref<Friend>();
const menuRef = ref<HTMLElement>();
const info_hover = ref<boolean>(false);
const hideTimeout = ref<number | null>(null);

function clearHideTimeout() {
  if (hideTimeout.value) {
    clearTimeout(hideTimeout.value);
    hideTimeout.value = null;
  }
}

function offInfoHover() {
  clearHideTimeout();
  hideTimeout.value = window.setTimeout(() => {
    info_hover.value = false;
    hoveredFriend.value = undefined;
  }, 200);
}

const closeMenu = () => {
  menuVisible.value = false;
};

watch(menuVisible, (isVisible) => {
  if (isVisible) {
    setTimeout(() => document.addEventListener('click', closeMenu), 0);
  } else {
    document.removeEventListener('click', closeMenu);
  }
});

async function handleContextmenu(row: Friend, event: MouseEvent) {
  menuPosition.value = { top: `${event.clientY}` + 'px', left: `${event.clientX}` + 'px' };
  selectedFriend.value = row;
  menuVisible.value = true;

  await nextTick();

  const menuElement = menuRef.value;
  if (!menuElement) return;

  const { innerWidth: windowWidth, innerHeight: windowHeight } = window;
  const { offsetWidth: menuWidth, offsetHeight: menuHeight } = menuElement;

  let newLeft = event.clientX;
  let newTop = event.clientY;

  if (newLeft + menuWidth > windowWidth) {
    newLeft = windowWidth - menuWidth - 10;
  }

  if (newTop + menuHeight > windowHeight) {
    newTop = windowHeight - menuHeight - 10;
  }

  menuPosition.value = { top: `${newTop}` + "px", left: `${newLeft}` + "px" };
}

const handleMenuClick = (action: 'profile' | 'message' | 'block' | 'note') => {
  if (!selectedFriend.value) return;

  switch (action) {
    case 'profile':
      routToFriendMypage(selectedFriend.value);
      break;
    case 'message':
      // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      break;
    case 'block':
      friendshipBlock(selectedFriend.value.fuuid).then(() => {
        ElMessage({ message: `${selectedFriend.value?.fname}` + " is blocked.", type: 'success'});
        friendsList.value?.dtoList.slice(selectedFriend.value?.fid, 1);
      })
      break;
    case "note":
      friendshipNote(selectedFriend.value.fid).then((res) => {
        ElMessage({ message: "Your note has been saved.", type: 'success'});
        if (friendsList.value?.dtoList) {
          const updatedFriend = friendsList.value.dtoList.find(friend => friend.fid === selectedFriend.value?.fid);
          if (updatedFriend) {
            updatedFriend.note = res;
          }
        }})
      break;
  }
  closeMenu();
};

async function handleInfoHover(f: Friend, event: MouseEvent) {
  clearHideTimeout();
  infoHoverPosition.value = { top: `${event.clientY}` + 'px', left: `${event.clientX}` + 'px' };
  info_hover.value = true;
  hoveredFriend.value = f;

  await nextTick();

  const infoHoverElement = infoHoverRef.value;
  if (!infoHoverElement) return;

  const { innerWidth: windowWidth, innerHeight: windowHeight } = window;
  const { offsetWidth: infoHoverWidth, offsetHeight: infoHoverHeight } = infoHoverElement;

  let newLeft = event.clientX;
  let newTop = event.clientY;

  if (newLeft + infoHoverWidth > windowWidth) {
    newLeft = windowWidth - infoHoverWidth - 10;
  }

  if (newTop + infoHoverHeight > windowHeight) {
    newTop = windowHeight - infoHoverHeight - 10;
  }

  infoHoverPosition.value = { top: `${newTop}` + "px", left: `${newLeft}` + "px" };
}
</script>
<template>
  <!-- pop-up list -->
  <transition name="pop">
    <div v-if="showFriendList" style="z-index: 9999; display: flex; flex-direction: column; position: fixed; bottom: 6em; right: 20px; width: 25em; height: 80vh; border-radius: 0.5em; box-shadow: 0.1em 0.1em 1em 0.1em #878787; padding: 1em; margin: 0.5em 0 0.5em 0; background-color: rgb(18,18,18);">
      <div style="display: flex; justify-content: center; border-bottom: 1px solid #434343; padding-bottom: 0.4em">
        <el-text style="font-size: 1.3em; font-weight: bold; color: #00DE64;">Friend</el-text>
        <!-- Popup close button -->
        <el-button v-if="showFriendList" link style="position: absolute; right: 1em; top: 1.3em;" @click="showFriendList = false">X</el-button>
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
.scroll {
  flex: 1;
  overflow-y: auto;
  height: max-content;
}
.friends {
  padding: 0;
  margin: 0;
}
.friends-item {
  width: 100%;
  list-style-type: none;
  padding: 0.5em;
  border-bottom: 1px solid #434343;
}
.friends-item:last-child {
  border-bottom: none;
}

.friends-item:hover {
  background: #404040;
}

.context-menu {
  position: fixed;
  display: flex;
  z-index: 10000;
  background-color: #191919;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(115, 115, 115, 0.5);
  padding: 1em 0;
  min-width: 150px;
  gap: 10px;
  flex-direction: column;
}

.context-menu-item {
  height: fit-content;
  vertical-align: center;
  padding: 2px 1em;
  cursor: pointer;
  color: #ffffff;
  font-size: 1em;
}

.context-menu-item:hover {
  background-color: #454545;
  line-height: normal;
}

.info-hover {
  position: fixed;
  display: flex;
  z-index: 9999;
  background-color: #191919;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(115, 115, 115, 0.5);
  padding: 1em 0;
  min-width: 150px;
  gap: 10px;
  flex-direction: column;
}

.fList {
  display: flex;
  flex-direction: row;
}
.signature {
  font-size: 3em;
  width: 3em;
}
.fContent {
  align-self: flex-start;
  margin-left: 1em;
}

.note, .date {
  font-size: 0.9em;
  color: #bfbfbf
}
.note {
  max-width: 15ch;
}
.fInfo {
  display: flex;
  flex-direction: column;
  gap: 0.2em;
}
.name {
  font-size: 1.1em;
  margin-left: 14px;
  font-weight: bold;
  color: #4e9bff;
}
.intro {
  color: #b5b5b5;
  overflow: hidden;
  width: 17em;
  height: 6em;
}
</style>