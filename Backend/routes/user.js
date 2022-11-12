import express from "express";
import { body } from "express-validator";




import { login, register, putOnce, logout, AuthenticatedUser,Refresh,getUser } from "../controllers/user.js";

const router = express.Router();

router
  .route("/register")
  .post(
    body("username").isLength({ min: 5 }),
    body("phoneNumber"),
    body("email"),
    body("password").isLength({ min: 8 }),
  
   
    register
  );

  router
  .route("/login")
  .post(
    body("username"),
    
    body("password"),
   
    login
  );
  

router
  .route("/:id")
  .put(
   
    body("username").isLength({ min: 5 }),
    body("phoneNumber"),
    body("email"),
    body("password").isLength({ min: 8 }),
    putOnce
  );

  router.route("/loggedUser").get(AuthenticatedUser);
  router.route("/logout").get(logout);
router.route("/refreshToken").post(Refresh);
router.route("/details").get(getUser);


export default router;
