import mongoose from "mongoose";
import Category from "../models/category.js";
const { Schema, model } = mongoose;

const product = new Schema(
  {

    //poduct id
    product_id: {
      type: mongoose.Schema.Types.ObjectId
    },

    user_id: String,
    category: {
      type: String,
      required: true,
    },

    name: {
      type: String,
      required: true,
    },

    description: {
      type: String,
      required: true,
    },
    price: {
      type: Number,
      required: true,
    },
    image: String,

    quantity: {
      type: Number,
      default: 1,
    },
     added_date: { type: Number, default: new Date().getTime() },
     bid_event: { type: Boolean, default:false},
     start_bid: { type: Number, default: 0},
     for_bid: { type: Boolean, default: false },
    bid_end_date: { type: Number, default: new Date().getTime() },
     bade: { type: Boolean, default: false },
    sold: { type: Boolean, default: false },
   

  },
  {
    timestamps: true,
  }
);

export default model("Product", product);

