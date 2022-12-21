import mongoose from "mongoose";
const { Schema, model } = mongoose;
const notificationSchema = new Schema(
  {
    title: {
      type: String,
      required: true,
    },
    description: {
      type: String,
      required: true,
    },
    date: {
      type: Number,
      default: new Date().getTime(),
    },
  },
  {
    timestamps: true,
  }
);
export default model("Notification", notificationSchema);
