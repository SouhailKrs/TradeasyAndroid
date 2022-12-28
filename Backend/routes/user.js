import express from "express";
import { body } from "express-validator";
import multer from "../middlewares/multer-config-single.js";

import {
  login,
  register,
  logout,
  verifyEmail,
  updatePassword,
  updateUsername,
  forgotPassword,
  resetPassword,
  verifyOtp,
  verifyUsername,
  uploadProfilePicture,
  getUserById,
  deleteUser,
  sendSms,
  getUserNotifications,
  deleteUserNotification,
} from "../controllers/user.js";
import { requireAuth } from "../middlewares/authMiddleware.js";

const router = express.Router();

router.route("/register").post(register);

router.route("/login").post(login);


router.route("/logout").get(logout);
router.route("/verify").post(verifyEmail);
router.route("/updatePassword").post(requireAuth, updatePassword);
router.route("/updateusername").post(requireAuth, updateUsername);
router.route("/forgotPassword").post(forgotPassword);
router.route("/resetpassword").post(resetPassword);
router.route("/verifyOtp").post(verifyOtp);
router.route("/verifyusername").post(verifyUsername);
router.route("/uploadprofilepicture").post(requireAuth, multer("image"),uploadProfilePicture);
router.route("/getuserbyid").post(getUserById);
router.route("/deleteaccount").post(requireAuth , deleteUser);
router.route("/sendsms").post(sendSms);
router.route("/getnotifications").get(requireAuth,getUserNotifications);
router.route("/deletenotification").post(requireAuth,deleteUserNotification);

export default router;
