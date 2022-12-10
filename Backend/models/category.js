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
        products: [{
            type: Schema.Types.ObjectId,
            ref: 'product'
        }]
       
    },
    {
        timestamps: true
    }
);

export default model('Category', categorySchema);
