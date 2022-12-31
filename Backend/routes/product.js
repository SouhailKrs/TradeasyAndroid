import express from "express";

import {
  addProdToSaved,
  addProduct,
  getProductsByCategory,
  findProductByName,
  getAllProducts,
  getProductsForBid,
  getSavedProds,
  getUserProducts,
  usernameProducts,
  userSelling,
  recentlyViewed,
  unlistProduct,
  deleteProduct,
  editProduct,
  recentlyAddedProducts,
} from "../controllers/productController.js";
import { requireAuth } from "../middlewares/authMiddleware.js";
import multer from "../middlewares/multer-config.js";

const router = express.Router();

router.route("/user/add").post(requireAuth, multer("image"), addProduct);
router.route("/userproducts").get(requireAuth, getUserProducts); //change it in frontend
router.route("/getall").get(getAllProducts);
router.route("/usernameProducts").post(usernameProducts);
router.route("/findbycat").post(requireAuth,getProductsByCategory);
router.route("/productsforbid").get(requireAuth, getProductsForBid);
router.route("/searchbyname").post(requireAuth,findProductByName);
router.route("/addprodtosaved").post(requireAuth, addProdToSaved);
router.route("/getsavedprods").get(requireAuth, getSavedProds);
router.route("/userselling").get(requireAuth, userSelling);
router.route("/recentlyviewed").post(requireAuth, recentlyViewed);
router.route("/unlistproduct").post(requireAuth, unlistProduct);
router.route("/deleteproduct").post(requireAuth, deleteProduct);
router.route("/recentlyadded").get(requireAuth, recentlyAddedProducts);
router.route("/editproduct").post(requireAuth,multer("image"),editProduct);


export default router;
