const mongoose = require("mongoose");
const Schema = mongoose.Schema;
const resetPasswordSchema = new Schema({
    username: {
        type: Schema.Types.ObjectId,
        required: true,
        ref: "user",
    },
    token: {
        type: String,
        required: true,
    },
    createdAt: {
        type: Date,
        default: Date.now,
        expires: 3600,
    },
});
module.exports = mongoose.model("Token", resetPasswordSchema);