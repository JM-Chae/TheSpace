<script lang="ts" setup>
import {UserFilled} from "@element-plus/icons-vue";
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

const {friendsList} = storeToRefs(useFriendsStore());
const popupRef = ref<HTMLElement | null>(null);

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
const menuVisible = ref(false);
const selectedFriend = ref<Friend>();
const menuRef = ref<HTMLElement>();

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

const showFriendList = ref(false);
</script>
<template>
  <!-- show button -->
  <div style="display: flex; flex-direction: column; align-items: end;">
    <transition name="fade">
      <div v-if="!showFriendList" style="z-index: 9997; position: fixed; bottom: 1em; right: calc(60px + 8em); border-radius: 0.5em; box-shadow: 0.1em 0.1em 1em 0.1em #878787; height: 4em; width: 4em; padding: 0; margin: 0.5em 0 0.5em 0; background-color: rgb(18,18,18);" >
        <el-button link style="display: flex; width: 4em; height: 4em; margin: 0;" @click="showFriendList = true"><el-icon style="font-size: 2em; position: absolute; right: 16px; top: 16px;"><UserFilled /></el-icon></el-button>
      </div>
    </transition>
  </div>

  <!-- pop-up list -->
  <transition name="pop">
    <div v-if="showFriendList" style="z-index: 9999; display: flex; flex-direction: column; position: fixed; bottom: 1em; right: 20px; width: 25em; height: 80vh; border-radius: 0.5em; box-shadow: 0.1em 0.1em 1em 0.1em #878787; padding: 1em; margin: 0.5em 0 0.5em 0; background-color: rgb(18,18,18);">
      <div style="display: flex; justify-content: center; border-bottom: 1px solid #434343; padding-bottom: 0.4em">
        <el-text style="font-size: 1.3em; font-weight: bold; color: #00DE64;">Friend</el-text>
        <!-- Popup close button -->
        <el-button v-if="showFriendList" link style="position: absolute; right: 1em; top: 1.3em;" @click="showFriendList = false">X</el-button>
      </div>
      <div class="scroll">
        <ul v-if="friendsList" class="friends" @contextmenu.prevent>
          <li v-for="friend in friendsList.dtoList" class="friends-item" @dblclick="routToFriendMypage(friend)">
            <div class="fList" @contextmenu.prevent="handleContextmenu(friend, $event)">
              <el-text class="signature">{{friend.fsignature}}</el-text>
              <div class="fInfo">
                <el-text class="fContent name" >{{friend.fname}}#{{friend.fuuid}}</el-text>
                <el-text class="fContent note">{{friend.note}}</el-text>
                <el-text class="fContent date">Since, {{formatDate(friend.acceptedAt)}}</el-text>
              </div>
            </div>
          </li>
        </ul>
     </div>
    </div>
  </transition>

  <infinite-loading v-if="showFriendList" @infinite="infiniteHandler">
    <template #complete><span> </span></template>
  </infinite-loading>

  <div v-if="menuVisible" ref="menuRef" :style="{ top: menuPosition.top, left: menuPosition.left }" class="context-menu">
    <div class="context-menu-item" @click="handleMenuClick('profile')">Move to Profile</div>
    <div class="context-menu-item" @click="handleMenuClick('message')">Send message</div>
    <div class="context-menu-item" @click="handleMenuClick('note')">Memo</div>
    <div class="context-menu-item" @click="handleMenuClick('block')">Block</div>

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


.fList {
  display: flex;
  flex-direction: row;
}
.signature {
  font-size: 3em;
}
.fContent {
  align-self: flex-start;
  margin-left: 1em;
}
.name {
  font-size: 1.1em;
  font-weight: bold;
  color: #4e9bff;
}
.note, .date {
  font-size: 0.9em;
  color: #bfbfbf
}
.fInfo {
  display: flex;
  flex-direction: column;
  gap: 0.2em;
}
</style>