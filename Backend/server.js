import express from "express";
import mongoose from "mongoose";
import morgan from "morgan";
import cors from "cors";
import session from "express-session";
import cookieParser from "cookie-parser";
import {errorHandler, notFoundError} from "./middlewares/error-handler.js";
import userRoutes from "./routes/user.js";
import categoryRoutes from "./routes/category.js";
import productRoutes from "./routes/product.js";
import bidRoutes from "./routes/bid.js";



const app = express();
const port = process.env.PORT || 9090;
const databaseName = "tradeasy";

mongoose.set("debug", true);
mongoose.Promise = global.Promise;

const dbOnline = `mongodb+srv://souhail:xDGlTo3PN9NSaFF2@cluster0.mquygiu.mongodb.net/${databaseName}`;
const dbOffline = `mongodb://0.0.0.0:27017/${databaseName}`;



mongoose
    .connect(dbOffline)
    .then(() => {
        console.log(`Connected to ${databaseName}`);
    })
    .catch((err) => {
        console.log("erroe " + err);
    });

    // get image
app.use('/img',express.static("public/images"));
app.use(cors());
app.use(morgan("dev"));
app.use(express.json());
// user session
app.use(cookieParser());
app.use(
    session({
        secret: "secret",
        saveUninitialized: true,
        resave: true,
    })
);
app.use(express.urlencoded({extended: true}));

app.use("/user", userRoutes);
app.use("/category", categoryRoutes);
app.use("/product", productRoutes);
app.use("/bid", bidRoutes);

app.use(notFoundError);

app.use(errorHandler);

app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}/`);
});


