<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import router from '@/router'
import { useRoute } from 'vue-router'

const route = useRoute()
const fileUri = ref<string>('')
if (route != undefined && route.query != undefined && route.query.fileUri != undefined) {
  fileUri.value = route.query.fileUri.toString()
}

/**
 * Cookie Authn Form
 */

const usernameError = ref<string>('')
const showUserNameError = ref<boolean>(false)
const passwordError = ref<string>('')
const showPasswordError = ref<boolean>(false)

const cookieAuthnFormRef = ref<FormInstance>()

const cookieAuthnForm = reactive({
  username: '',
  password: ''
})

const cookieAuthnRules = reactive<FormRules<typeof cookieAuthnForm>>({
  username: [
    { required: true, message: 'Please input username', trigger: 'blur' },
    { min: 1, max: 80, message: 'Length should be 1 to 80', trigger: 'blur' },
  ],
  password: [
    { required: true, message: 'Please input password', trigger: 'blur' },
    { min: 5, max: 40, message: 'Length should be 5 to 40', trigger: 'blur' },
  ]
})

const submitCookieAuthnForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
      resetErrMsg()
      cookieAuthn(cookieAuthnForm)
    } else {
      return false
    }
  })
}

const cookieAuthn = (cookieAuthnForm: { username: string; password: string }) => {
  setTimeout(async () => {
    await axios({ method: 'post',
                  url: '/openapi/control/cookieAuthn',
                  params: {
                    'username': cookieAuthnForm.username,
                    'password': cookieAuthnForm.password
                  },
                })
                .then(response => {
                  if (response.status == 200 && response.data && response.data.error) {
                    let errMsg = JSON.stringify(response.data.error).replaceAll('"', '')
                    ElMessage({
                            type: 'error',
                            message: errMsg,
                            // duration: 4000
                          })
                    if (errMsg.toLocaleLowerCase().indexOf("user") != -1) {
                      usernameError.value = errMsg
                      showUserNameError.value = true
                    }
                    if (errMsg.toLocaleLowerCase().indexOf("password") != -1) {
                      passwordError.value = errMsg
                      showPasswordError.value = true
                    }
                  } else {
                    ElMessage({
                              type: 'success',
                              message: 'Logged in successfully.'
                            })
                    if (fileUri.value != '') {
                      router.push({path: '/viewfile', query: {fileUri: fileUri.value}})
                    } else {
                      router.push({path: '/viewfile'})
                    }
                  }
                })
                .catch(reason => {
                  ElMessage({
                              type: 'error',
                              message: reason.message,
                            })
                })
  }, 500)
}

const resetErrMsg = () => {
  usernameError.value = ''
  showUserNameError.value = false
  passwordError.value = ''
  showPasswordError.value = false
}

// end of cookie authn

</script>

<template>
  <!-- partial:index.partial.html -->
  <div id="back">
    <canvas id="canvas" class="canvas-back"></canvas>
    <div class="backRight">    
    </div>
    <div class="backLeft">
    </div>
  </div>

  <div id="slideBox">
    <div class="topLayer">
      <div class="left">
        <div class="content">
          <h2>Sign Up</h2>
          <el-form id="form-signup" method="post">
            <el-form-item>
              <div class="form-element form-stack">
                <label for="email" class="form-label">Email</label>
                <input id="email" type="email" name="email" autocomplete="off">
              </div>
            </el-form-item>
            <el-form-item>
              <div class="form-element form-stack">
                <label for="username-signup" class="form-label">Username</label>
                <input id="username-signup" type="text" name="username" autocomplete="off">
              </div>
            </el-form-item>
            <el-form-item>
              <div class="form-element form-stack">
                <label for="password-signup" class="form-label">Password</label>
                <input id="password-signup" type="password" name="password">
              </div>
            </el-form-item>
            <el-form-item>
              <div class="form-element form-checkbox">
                <input id="confirm-terms" type="checkbox" name="confirm" value="yes" class="checkbox">
                <label for="confirm-terms">I agree to the <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a></label>
              </div>
            </el-form-item>
            <el-form-item>
              <div class="form-element form-submit">
                <el-button id="signUp" class="signup" name="signup">Sign up</el-button>
                <el-button id="goLeft" class="signup off">Log In</el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div class="right">
        <div class="content">
          <h2>Login</h2>
          <el-form ref="cookieAuthnFormRef"
            :model="cookieAuthnForm"
            :rules="cookieAuthnRules">
            <el-form-item prop="username"
              :error="usernameError"
              :show-message="showUserNameError">
              <div class="form-element form-stack">
                <label for="username-login" class="form-label">Username</label>
                <el-input v-model="cookieAuthnForm.username"
                  maxlength="80"
                  minlength="1"
                  id="username-login"/>
              </div>
            </el-form-item>
            <el-form-item prop="password"
              :error="passwordError"
              :show-message="showPasswordError">
              <div class="form-element form-stack">
                <label for="password-login" class="form-label">Password</label>
                <el-input 
                  v-model="cookieAuthnForm.password"
                  autocomplete="off"
                  type="password"
                  maxlength="40"
                  minlength="5"
                  id="password-login"/>
              </div>
            </el-form-item>
            <el-form-item class="form-element form-submit">
              <div class="form-element form-submit">
                <el-button class="login" @click="submitCookieAuthnForm(cookieAuthnFormRef)">Log In</el-button>
                <!-- <el-button id="goRight" class="login off" name="signup">Sign Up</el-button> -->
              </div>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import '@/assets/login/login.css'
import $ from 'jquery'
import paper from 'paper'
import { Group, Path, Point } from 'paper'
import axios from 'axios'

export default {
  mounted() {
    /* ====================== *
    *  Toggle Between        *
    *  Sign Up / Login       *
    * ====================== */
    // $(document).ready(function(){
    $(function(){
      $('#goRight').on('click', function(){
        $('#slideBox').animate({
          'marginLeft' : '0'
        });
        $('.topLayer').animate({
          'marginLeft' : '100%'
        });
      });

      $('#goLeft').on('click', function(){
        if (window.innerWidth > 769){
          $('#slideBox').animate({
            'marginLeft' : '50%'
          })
        } else {
          $('#slideBox').animate({
            'marginLeft' : '20%'
          })
        }
        $('.topLayer').animate({
          'marginLeft': '0'
        })
      })
    })

    /* ====================== *
    *  Initiate Canvas       *
    * ====================== */
    // paper.install(window);
    paper.setup(document.getElementById("canvas") as HTMLCanvasElement)
    // paper.setup(document.getElementById("canvas"));

    // Paper JS Variables
    let canvasWidth: number,
        canvasHeight: number,
        canvasMiddleX: number,
        canvasMiddleY: number

    const shapeGroup = new Group()

    let positionArray: paper.Point[] = []

    function getCanvasBounds() {
      // Get current canvas size
      canvasWidth = paper.view.size.width
      canvasHeight = paper.view.size.height
      // canvasWidth = view.size.width;
      // canvasHeight = view.size.height;
      canvasMiddleX = canvasWidth / 2
      canvasMiddleY = canvasHeight / 2
      // Set path position
      const position1 = new Point({
        x: (canvasMiddleX / 2) + 100,
        y: 100, 
      })

      const position2 = new Point({
        x: 200,
        y: canvasMiddleY, 
      })

      const position3 = new Point({
        x: (canvasMiddleX - 50) + (canvasMiddleX / 2),
        y: 150, 
      })

      const position4 = new Point({
        x: 0,
        y: canvasMiddleY + 100, 
      })

      const position5 = new Point({
        x: canvasWidth - 130,
        y: canvasHeight - 75, 
      })

      const position6 = new Point({
        x: canvasMiddleX + 80,
        y: canvasHeight - 50, 
      })
      
      const position7 = new Point({
        x: canvasWidth + 60,
        y: canvasMiddleY - 50, 
      })
      
      const position8 = new Point({
        x: canvasMiddleX + 100,
        y: canvasMiddleY + 100, 
      })

      positionArray = [position3, position2, position5, position4, position1, position6, position7, position8]
    }

    /* ====================== *
    * Create Shapes          *
    * ====================== */
    function initializeShapes() {
      // Get Canvas Bounds
      getCanvasBounds()

      const shapePathData = [
        'M231,352l445-156L600,0L452,54L331,3L0,48L231,352', 
        'M0,0l64,219L29,343l535,30L478,37l-133,4L0,0z', 
        'M0,65l16,138l96,107l270-2L470,0L337,4L0,65z',
        'M333,0L0,94l64,219L29,437l570-151l-196-42L333,0',
        'M331.9,3.6l-331,45l231,304l445-156l-76-196l-148,54L331.9,3.6z',
        'M389,352l92-113l195-43l0,0l0,0L445,48l-80,1L122.7,0L0,275.2L162,297L389,352',
        'M 50 100 L 300 150 L 550 50 L 750 300 L 500 250 L 300 450 L 50 100',
        'M 700 350 L 500 350 L 700 500 L 400 400 L 200 450 L 250 350 L 100 300 L 150 50 L 350 100 L 250 150 L 450 150 L 400 50 L 550 150 L 350 250 L 650 150 L 650 50 L 700 150 L 600 250 L 750 250 L 650 300 L 700 350 '
      ];

      for (let i = 0; i <= shapePathData.length; i++) {
        // Create shape
        const headerShape = new Path({
          strokeColor: 'rgba(255, 255, 255, 0.5)',
          strokeWidth: 2,
          parent: shapeGroup,
        })
        // Set path data
        headerShape.pathData = shapePathData[i]
        headerShape.scale(2)
        // Set path position
        headerShape.position = positionArray[i]
      }
    }

    initializeShapes()

    /* ====================== *
    * Animation              *
    * ====================== */
    paper.view.onFrame = function paperOnFrame(event: { count: number }) {
      if (event.count % 4 === 0) {
        // Slows down frame rate
        for (let i = 0; i < shapeGroup.children.length; i++) {
          if (i % 2 === 0) {
            shapeGroup.children[i].rotate(-0.1)
          } else {
            shapeGroup.children[i].rotate(0.1)
          }
        }
      }
    }

    paper.view.onResize = paperOnResize
    
    function paperOnResize() {
      getCanvasBounds();

      for (let i = 0; i < shapeGroup.children.length; i++) {
        shapeGroup.children[i].position = positionArray[i]
      }

      if (canvasWidth < 768) {
        shapeGroup.children[3].opacity = 0
        shapeGroup.children[2].opacity = 0
        shapeGroup.children[5].opacity = 0
      } else {
        shapeGroup.children[3].opacity = 1
        shapeGroup.children[2].opacity = 1
        shapeGroup.children[5].opacity = 1
      }
    }

    window.addEventListener("resize", paperOnResize)
  },
}
</script>

<style scoped>
.el-form-item {
  margin-bottom: 0px;
}

.form-checkbox label {
  font-size: 0.9em;
  line-height: 1.1em;
}

label {
  line-height: 1.1em;
}

button {
  color: #fff;
  height: 42px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.6px;
  vertical-align: unset;
  border: unset;
}

.is-error .right input {
  border-bottom: 0px;
}
</style>

<style>
.el-input__wrapper {
  padding: 0px;
  background-color: unset;
}

.el-form-item__error {
  top: 85%;
}

.el-input__wrapper,
.el-input__wrapper:hover,
.el-input__wrapper.is-focus {
  box-shadow: none;
}

.el-form-item.is-error .el-input__wrapper,
.el-form-item.is-error .el-input__wrapper.is-focus {
  box-shadow: none !important;
}

.is-error .right .el-input__inner {
  border-bottom: 0px;
}
</style>