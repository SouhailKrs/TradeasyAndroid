import mongoose from "mongoose";
const { Schema, model } = mongoose;

const product = new Schema(
  {
    //poduct id
    product_id: {
      type: mongoose.Schema.Types.ObjectId,
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
    image: {
      type: Array,
      required: false,
    },

    quantity: {
      type: Number,
      default: 1,
    },
    added_date: { type: Number, default: new Date().getTime() },
    for_bid: { type: Boolean, default: false },
    bid_end_date: { type: Number },
    bade: { type: Boolean, default: false },
    sold: { type: Boolean, default: false },
    username: { type: String },
    userPhoneNumber: { type: String },
    userProfilePicture: { type: String },
    selling: { type: Boolean, default: false }, 


  },
  {
    timestamps: true,
  }
);

export default model("Product", product);
