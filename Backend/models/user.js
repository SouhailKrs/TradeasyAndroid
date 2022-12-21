import mongoose from "mongoose";
import notification from "./notification.js";

const { Schema, model } = mongoose;

const userSchema = new Schema(
  {
    username: {
      type: String,
      required: true,
      unique: true,
    },
    phoneNumber: {
      type: Number,
      required: true,
      unique: true,
    },
    email: {
      type: String,
      required: true,
      unique: true,
    },
    password: {
      type: String,
      required: true,
    },
    profilePicture: {
      type: String,
      required: false,
    },
    isVerified: {
      type: Boolean,
      default: false,
    },
    notificationToken: {
      type: String,
      required: false,
    },
    notifications: [notification.schema],

    savedProducts: {
      type: Array,
      required: false,
    },
    otp: {
      type: Number,
      required: false,
      default: 0,
    },
    countryCode : {
      type: String,
      required: true,
    },
  },
  {
    timestamps: true,
  }
);

export default model("User", userSchema);
