import mongoose from 'mongoose';
import User from "../models/user.js";
const { Schema, model } = mongoose;


const categorySchema = new Schema(
    {
        name: {
            type: String,
            required: true,
            unique:true
    
        },
      
       
    },
    {
        timestamps: true
    }
);

export default model('Category', categorySchema);
