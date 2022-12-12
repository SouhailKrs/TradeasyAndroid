
import Category from "../models/category.js";






// add category
export const addCategory = async (req, res) => {
    const { name } = req.body;
    try {
        const category = await Category.findOne({ name });
        if (category) {
            return res.status(400).json({ msg: 'Category already exists' });
        }
        const newCategory = new Category({ name });
        await newCategory.save();
        res.json({ msg: 'Category created' });
    } catch (err) {
        return res.status(500).json({ msg: err.message });
    }
};




  







        




