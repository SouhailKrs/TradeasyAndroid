import mongoose from 'mongoose';
const { Schema, model } = mongoose;

const userSchema = new Schema(
    {
        username: {
            type: String,
            required: true,
            unique:true
        },
        phoneNumber: {
            type: Number,
            required: true,
            unique:true
        },
        email: {
            type: String,
            required: true,
            unique:true
        },
        password: {
            type: String,
            required: true
        },
        profilePicture: {
            type: String,
            required: false
        },
       
    },
    {
        timestamps: true
    }
);

export default model('User', userSchema);