import { config } from "dotenv";
import express from "express";
import { addProduct, getAllProducts, getProducts,userProducts,filterProducts , getProductsForBid } from "../controllers/productController.js";
import { requireAuth } from "../middlewares/authMiddleware.js";
import multer from "../middlewares/multer-config.js";

const router = express.Router();

router.route("/user/add").post(requireAuth,addProduct);
router.route("/get").get(getProducts);
router.route("/getall").get(getAllProducts);
router.route("/userProducts").post(userProducts);
router.route("/findbycat").get(filterProducts);
router.route("/productsforbid").get(getProductsForBid);
export default router;
