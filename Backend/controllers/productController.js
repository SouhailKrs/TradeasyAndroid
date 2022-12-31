import Category from "../models/category.js";
import Product from "../models/product.js";
import User from "../models/user.js";







//edit product
export const editProduct = async (req, res) => {
  try {
    let bid_end_date = req.body.bid_end_date;
    let cat = await Category.findOne ({name:req.body.category});
    
    const imagePath = `${req.protocol}://${req.get("host")}/img/`;
    if (!cat){
      return res.status(400).json({ "message": "please select a category" });
    } else {
      await (async () => {
        if (bid_end_date === "1 Hour") {
          bid_end_date = new Date().getTime() + 3600000;
        } else if (bid_end_date === "1 Day") {
          bid_end_date = new Date().getTime() + 86400000;
        } else if (bid_end_date === "1 Week") {
          bid_end_date = new Date().getTime() + 86400000 * 7;
        }
        const product = await Product.findOne({ prod_id : req.body.prod_id, user_id: req.user._id });
        if (product) {
          product.name = req.body.name;
          product.description = req.body.description;
          product.price = req.body.price;
          
          product.image =
            req.files.map((file) => {
            return imagePath + file.filename;
          }),
          product.category = req.body.category,
          product.quantity = req.body.quantity,
          product.added_date = new Date().getTime(),
          product.bid_end_date = bid_end_date,
          product.for_bid = req.body.for_bid,
          product.username = req.user.username,
          product.userPhoneNumber = req.user.phoneNumber,
          product.userProfilePicture = req.user.profilePicture,
          product.selling = true,
          product.updateOne(product).then((product) => {
            res.json({ data: product });
          });
        } else {
          res.status(400).json({ "message": "product not found" });
        }
      })();
    }
  } catch (error) {
    res.status(500).json(error.message);
  }
};
// ADD PRODUCT
export const addProduct = async (req, res) => {
  try { 
    if(req.user!==null){
 
    let bid_end_date = req.body.bid_end_date;
    const cat = await Category.findOne({ name: req.body.category });
    const imagePath = `${req.protocol}://${req.get("host")}/img/`;
    if (!cat) {
      return res.status(400).json({ "message": "please select a category" });
    } else {
      await (async () => {
        if (bid_end_date === "1 Hour") {
          bid_end_date = new Date().getTime() + 3600000;
        } else if (bid_end_date === "1 Day") {
          bid_end_date = new Date().getTime() + 86400000;
        } else if (bid_end_date === "1 Week") {
          bid_end_date = new Date().getTime() + 86400000 * 7;
        }

        const newProduct = new Product({
          user_id: req.user._id,
          name: req.body.name,
          description: req.body.description,
          price: req.body.price,
          image:
            // add image path for each image
            req.files.map((file) => {
              return imagePath + file.filename;
            }),

          category: req.body.category,
          quantity: req.body.quantity,
          added_date: new Date().getTime(),
          bid_end_date: bid_end_date,
          for_bid: req.body.for_bid,
          username: req.user.username,
          userPhoneNumber: req.user.phoneNumber,
          userProfilePicture: req.user.profilePicture,
          selling:true,
        });


        await newProduct.save().then((product) => {
          res.json({ data: product });
        });
      })();
    }
  
    }else{
      res.status(400).json({ "message": "please login" });
    }
  } catch (error) {
    res.status(500).json(error.message);
  }
};

// GET PRODUCT OF CONNECTED USER
export const getUserProducts = async (req, res) => {
  try {
    // search user products in product collection
    const products = await Product.find({ user_id: req.user._id ,selling:false });
    res.json({ data: products });
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// GET PRODUCT BY SEARCH OF USERNAME
export const usernameProducts = async (req, res) => {
  try {
    const { username } = req.body;
    const user = await User.findOne({ username: username });

    if (!user) {
      return res.status(400).json({ "message": "user not found" });
    } else {
      const products = await Product.find({ user_id: user._id });
      res.json({ data: products });
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// GET ALL PRODUCTS
export const getAllProducts = async (req, res) => {
  try {
    const products = await Product.find();
    res.json(products);
  } catch (err) {
    return res.status(500).json({ "msg": err.message });
  }
};

// fin product by name and return empty array if not found
export const findProdudctByName = async (req, res) => {
  const { name } = req.body;
  // check if name is empty
  try {
    const products = await Product.find({
      name: { $regex: "^" + name, $options: "i" },
    }).limit(10);
    // return empty array if not found and name is empty
    if (products.length === 0 || name === "") {
      res.send({ data: [] });
    } else {
      res.send({ data: products });
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// add product to saved items
export const addProdToSaved = async (req, res) => {
  try {
    const { product_id } = req.body;

    const product = await Product.findOne({ _id: product_id });
    // check if product already exists in saved items
    const user = req.user;
    const savedProducts = user.savedProducts;
    if (savedProducts.length > 0) {
      for (var i = 0; i < savedProducts.length; i++) {

        var alreadyExists = false;
        if (savedProducts[i]._id.toString() == product._id) {
          console.log("product already exists");
          alreadyExists = true;

        }
      }


      if (alreadyExists) {

        return res.status(400).json({ "message": "product already exists" });
      }
      else {
        user.savedProducts.push(product);
        await user.save();
        res.send({ data: user });
      }
    } else {
      user.savedProducts.push(product);
      await user.save();
      res.send({ data: user });
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};
// get saved products
export const getSavedProds = async (req, res) => {
  try {
    const user = await req.user;
    res.send({ data: user.savedProducts });
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// find products  were sold is false of connected user
export const userSelling = async (req, res) => {
  try {
    const products = await Product.find({ user_id: req.user._id, selling: true });
    res.send({ data: products });
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};


// fucntion to add product to recently viewed and check if product already exists


// add product to recently viewed
export const recentlyViewed = async (req, res) => {
  try {
    const { product_id } = req.body;

    const product
      = await Product.findOne({ _id: product_id });
    // check if product already exists in saved items
    const user = req.user;
    const recentProducts = user.recentlyViewed;
    if (recentProducts.length > 0) {

      for (var i = 0; i < recentProducts.length; i++) {

        var alreadyExists = false;
        if (recentProducts[i]._id.toString() == product._id) {
          console.log("product already exists");
          alreadyExists = true;

        }
      }


      if (alreadyExists) {

        return res.status(400).json({ "message": "product already exists" });
      } else {
        if (recentProducts.length > 2) {
          user.recentlyViewed.shift();
          user.recentlyViewed.push(product);
          await user.save();
          res.send({ data: user });
        }
        else {
          user.recentlyViewed.push(product);
          await user.save();
          res.send({ data: user });
        }
      }

    }
    else {
      user.recentlyViewed.push(product);
      await user.save();
      res.send({ data: user });
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};



// get products by category limit 10 except of the connected user and if user in not connected get all products
export const getProductsByCategory = async (req, res) => {
  try {
    const { category } = req.body;
    if (req.user) {
      const products = await Product.find
        ({
          category
            : category, selling:true , sold:false , user_id: { $ne: req.user._id}
        }).limit(10);
     
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet in this category" });
      }
      else {
  
        res.send({ data: products });
      }
    } else {
    
      const products = await Product.find
        ({
          category
            : category
        }).limit(10);
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet in this category" });
      }
      else {

        res.send({ data: products });
      }
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// get products by for bid  limit 10 except of the connected user and if user in not connected get all products for bid 
export const getProductsForBid = async (req, res) => {
  try {
    if (req.user) {
      const products = await Product.find
        ({
          forBid: true, selling:true,  user_id: { $ne: req.user._id }
        }).limit(10);
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet for bid" });
      }
      else {
        res.send({ data: products });
      }
    } else {
      const products = await Product.find
        ({
          forBid: true,
          selling:true,
        }).limit(10);
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet for bid" });
      }
      else {
        res.send({ data: products });
      }
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
}


// search products by name if user is connected get all products except of the connected user else get all products
export const findProductByName = async (req, res) => {
  try {
    const { name } = req.body;
    if (req.user) {
      const products = await Product.find
        ({
          name: { $regex: "^" + name, $options: "i"  }, selling:true , sold:false , user_id: { $ne: req.user._id}
        });
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet"  });
      }
      else {
        res.send({ data: products });
      }
    } else {
      const products = await Product.find
        ({
          name: { $regex: "^" + name, $options: "i"  }
        });
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet" });
      }
      else {
        res.send({ data: products });
      }
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// remove product from selling
// 


export const unlistProduct = async (req, res) => {
  try {
    // search for product of the connected user


    const { product_id } = req.body;
    const product = await Product.findOne({ _id: product_id, user_id: req.user._id });
    if(product.selling==true){
      product.selling = false;
      await product.save();
      res.status(200).json({ "message": "product unlisted" });
 
    }
    else{
      product.selling = true;
      await product.save();
      res.status(200).json({ "message": "product unlisted" });
    
    }
   
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};
// user_id: req.user._id 
// delete product and check if product is in saved items or recently viewed and remove it
export const deleteProduct = async (req, res) => {
  try {
    const { product_id } = req.body;
    const product
      = await
        Product
          .
            findOne({ _id: product_id,});
    if (product) {
      console.log("aaaa 1")
      // check if product is in saved items
// delete this product from saved items of all users
  // get saved items of all users
  const users = await User.find();
  // loop on users
  users.forEach(async (user) => {
    console.log("aaaa 2")
    // loop on saved items
    user.savedProducts.forEach(async (savedProducts) => {
      console.log("aaaa 3")
      // check if product id is equal to the product id
   
      if (savedProducts._id.toString() == product_id && savedProducts.length > 0 ) {
        // remove product from saved items
        console.log("aaaa 4" + savedProducts)
         user.savedProducts.splice(user.savedProducts.indexOf(savedProducts), 1);
  
        await user.save();
      }
    });
  });

      await product.remove();
      res.status(200).json({ "message": "product not found" });
    }
    else {
      res.status(500).json({ "message": "product not found" });
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};
// get recently added products in the last 24 hours if the user is connected get all products except of the connected user else get all products
export const recentlyAddedProducts = async (req, res) => {
  try {
    const date = new Date();
    date.setDate(date.getDate() - 7);
    if (req.user) {
      console.log("aaaaa");
      const products = await Product.find
        ({
          added_date: { $gte: date }, selling:true , sold:false , user_id: { $ne: req.user._id}
        }).sort({ added_date: -1 }).limit(15);
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet" });
      }
      else {
        res.send({ data: products });
      }
    } else {
      console.log("bbbb");
      const products = await Product.find
        ({
          added_date: { $gte: date }
        }).sort({ added_date: -1 }).limit(15);

      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet" });
      }
      else {
        res.send({ data: products });
      }
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
}

// get recently added products in the last week if the user is connected get all products except of the connected user else get all products

// get all products and sort them by added date
