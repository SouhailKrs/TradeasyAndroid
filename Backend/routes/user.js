import express from "express";
import { body } from "express-validator";




import { login, register, putOnce, logout,verifyEmail,updatePassword, forgotPassword,verifyOtp} from "../controllers/user.js";
import{requireAuth} from "../middlewares/authMiddleware.js";

const router = express.Router();

router
  .route("/register")
  .post(

   
    register
  );

  router
  .route("/login")
  .post(
 
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


router.route("/logout").get(logout);
//router.route("/details").get(requireAuth);
router.route("/verify").post(verifyEmail);
router.route("/updatePassword").post(requireAuth,updatePassword);
router.route("/forgotPassword").post(forgotPassword);
router.route("/verifyOtp").post(verifyOtp);

  
export default router;
