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
  removeProductFromSelling,
  deleteProduct,
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
router.route("/removeproductfromselling").post(requireAuth, removeProductFromSelling);
router.route("/deleteproduct").post(requireAuth, deleteProduct);


export default router;
