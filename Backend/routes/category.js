import { body } from "express-validator";
import express from "express";

import { addCategory } from "../controllers/categoryController.js";
const router = express.Router();

router.route("/add").post(addCategory);


export default router;
