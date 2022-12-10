import mongoose from "mongoose";
const { Schema, model } = mongoose;
const bidSchema = new Schema(
    {
        user_id: String,
        product_id: String,
        bid_amount: Number,
    },
    { timestamps: true }
);

export default model("Bid", bidSchema);