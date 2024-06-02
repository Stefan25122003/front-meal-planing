import { useState } from "react";
import "./AddRecipeForm.css";

export const AddRecipeForm = ({ closeModal }) => {
  const [recipe, setRecipe] = useState({
    title: "",
    description: "",
    ingredients: [""],
    instructions: "",
  });
  const handleChange = (e) => {
    const { name, value } = e.target;
    setRecipe({ ...recipe, [name]: value });
  };

  const handleIngredientChange = (index, value) => {
    const updatedIngredients = [...recipe.ingredients];
    updatedIngredients[index] = value;
    setRecipe({ ...recipe, ingredients: updatedIngredients });
  };

  const addIngredient = () => {
    setRecipe({ ...recipe, ingredients: [...recipe.ingredients, ""] });
  };

  const removeIngredient = (index) => {
    if (recipe.ingredients.length > 1) {
      const updatedIngredients = [...recipe.ingredients];
      updatedIngredients.splice(index, 1);
      setRecipe({ ...recipe, ingredients: updatedIngredients });
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    closeModal();
  };
  return (
    <>
      <h2 className="modal-title">Add a new recipe</h2>
      <form onSubmit={handleSubmit} className="recipe-form">
        <div className="form-group">
          <label className="form-label">Title</label>
          <input
            type="text"
            name="title"
            value={recipe.title}
            onChange={handleChange}
            className="form-control"
            required
          />
        </div>
        <div className="form-group">
          <label className="form-label">Description</label>
          <textarea
            name="description"
            value={recipe.description}
            onChange={handleChange}
            className="form-control"
            rows="5"
            required
          />
        </div>
        <div className="form-group">
          <label className="form-label">
            Ingredients
            <button
              type="button"
              onClick={addIngredient}
              className="add-ingredient-button"
            >
              +
            </button>
          </label>
          <div className="ingredients">
            {recipe.ingredients.map((ingredient, index) => (
              <div key={index} className="ingredient-item">
                <input
                  type="text"
                  value={ingredient}
                  onChange={(e) =>
                    handleIngredientChange(index, e.target.value)
                  }
                  className="form-control"
                  required
                />
                <button
                  type="button"
                  onClick={() => removeIngredient(index)}
                  className="remove-ingredient-button"
                >
                  -
                </button>
              </div>
            ))}
          </div>
        </div>
        <div className="form-group">
          <label className="form-label">Instructions</label>
          <textarea
            name="instructions"
            value={recipe.instructions}
            onChange={handleChange}
            className="form-control"
            rows="5"
            required
          />
        </div>
        <div className="form-actions">
          <button
            type="button"
            onClick={closeModal}
            className="close-modal-button"
          >
            Close
          </button>
          <button type="submit" className="submit-button">
            Save
          </button>
        </div>
      </form>
    </>
  );
};
