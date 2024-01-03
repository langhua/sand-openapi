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
            <form id="form-signup" method="post" onsubmit="return false;">
            <div class="form-element form-stack">
                <label for="email" class="form-label">Email</label>
                <input id="email" type="email" name="email">
            </div>
            <div class="form-element form-stack">
                <label for="username-signup" class="form-label">Username</label>
                <input id="username-signup" type="text" name="username">
            </div>
            <div class="form-element form-stack">
                <label for="password-signup" class="form-label">Password</label>
                <input id="password-signup" type="password" name="password">
            </div>
            <div class="form-element form-checkbox">
                <input id="confirm-terms" type="checkbox" name="confirm" value="yes" class="checkbox">
                <label for="confirm-terms">I agree to the <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a></label>
            </div>
            <div class="form-element form-submit">
                <button id="signUp" class="signup" type="submit" name="signup">Sign up</button>
                <button id="goLeft" class="signup off">Log In</button> 
            </div>
            </form>
        </div>
        </div>
        <div class="right">
        <div class="content">
            <h2>Login</h2>
            <form id="form-login" method="post" onsubmit="return false;">
            <div class="form-element form-stack">
                <label for="username-login" class="form-label">Username</label>
                <input id="username-login" type="text" name="username">
            </div>
            <div class="form-element form-stack">
                <label for="password-login" class="form-label">Password</label>
                <input id="password-login" type="password" name="password">
            </div>
            <div class="form-element form-submit">
                <button id="logIn" class="login" type="submit" name="login">Log In</button>
                <button id="goRight" class="login off" name="signup">Sign Up</button>
            </div>
            </form>
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
