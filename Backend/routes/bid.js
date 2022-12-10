
import express from "express";
import { requireAuth } from "../middlewares/authMiddleware.js";

import { placeBid } from "../controllers/bidController.js";
const router = express.Router();

router.route("/place").post(requireAuth,placeBid);


export default router;
