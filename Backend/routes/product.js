import express from "express";

import {
  addProdToSaved,
  addProduct,
  buyNow,
  filterProducts,
  findProductByName,
  getAllProducts,
  getProductsForBid,
  getSavedProds,
  getUserProducts,
  usernameProducts,
  userSelling,
} from "../controllers/productController.js";
import { requireAuth } from "../middlewares/authMiddleware.js";
import multer from "../middlewares/multer-config.js";

const router = express.Router();

router.route("/user/add").post(requireAuth, multer("image"), addProduct);
router.route("/userproducts").get(requireAuth, getUserProducts); //change it in frontend
router.route("/getall").get(getAllProducts);
router.route("/usernameProducts").post(usernameProducts);
router.route("/findbycat").get(filterProducts);
router.route("/productsforbid").get(requireAuth, getProductsForBid);
router.route("/searchbyname").post(findProductByName);
router.route("/addprodtosaved").post(requireAuth, addProdToSaved);
router.route("/getsavedprods").get(requireAuth, getSavedProds);
router.route("/userselling").get(requireAuth, userSelling);
router.route("/buynow").post(requireAuth, buyNow);

export default router;
