import express from "express";

import {addCategory,getCategories} from "../controllers/categoryController.js";

const router = express.Router();

router.route("/add").post(addCategory);
router.route("/getcategories").get(getCategories);
export default router;
