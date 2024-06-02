import React, {useEffect, useState} from "react";
import "./modal.css";
import titleImage from './Photos/Lasagna.jpg';
import {fetchImage} from './pexelsApiFetch.js';

export default function Modal({ recipe, toggleModal, img }) {
    console.log('Modal rendered with recipe:', recipe);
    const [imageSrc, setImageSrc] = useState(null);
    useEffect(() => {
        if(recipe)
            if(img) setImageSrc(img);
            else if (recipe && recipe.imageList && recipe.imageList.length > 0) {
            setImageSrc(recipe.imageList[0]);
            } else {
            fetchImage(recipe.recipeTitle.includes("recipe") ? recipe.recipeTitle : recipe.recipeTitle + ' recipe')
                .then((url) => {
                setImageSrc(url);
                });
            }
      }, []);

    //console.log(recipe.imageList);//its ok
    //const imageSrc = (recipe.imageList && recipe.imageList.length > 0) ? recipe.imageList[0] : titleImage;
    console.log(recipe.instructionsList);
    
    return (
        <div className="modal">
            <div onClick={toggleModal} className="overlay"></div>
            <div className="modal-content">
                <h2>{recipe.recipeTitle}</h2>
                
                <div className=".modal-title-image-container"><img src={imageSrc} alt="Title Image" className="modal-title-image" /> </div>
                <div className="text-box-container">
                    <div className="text-box-left">
                    <h3>INGREDIENTS</h3>
                    <ul>
                        { recipe.printableIngredients.map( ingredient => <li>{ingredient.replace(/"/g, '')}</li>) }
                    </ul>
                    </div>
                    
                     {/* Add your left text box here */}
                    <div className="text-box-right">
                    <h3>PREPARATION METHOD</h3>
                    <div className="text-box-right">
                        {recipe.instructionsList && Object.keys(recipe.instructionsList).length > 0 ? (
                            <ol>
                            {Object.values(recipe.instructionsList).map(instruction => <li key={instruction}>{instruction}</li>)}
                            </ol>
                        ) : (
                            <p>{recipe.description}</p>
                        )}
                    </div>
                </div>
                <button className="close-modal" onClick={toggleModal}>
                    
                </button>
            </div>
        </div>
    </div>
    );
}





    // return (
    //     <div className="modal">
    //         <div onClick={toggleModal} className="overlay"></div>
    //         <div className="modal-content">
    //             <h2>Lasagna</h2>
    //             <img src={titleImage} alt="Title Image" className="modal-title-image"/> {/* Add your title image here */}
    //             <div className="text-box-container">
    //                 <div className="text-box-left">
    //                 <h3>INGREDIENTS</h3>
    //                 <ul>
    //                     <li>Lasagna noodles: 9-12 sheets (depending on the size of your baking dish)</li>
    //                     <li>Ground beef, turkey, or sausage: 1 pound (450g)</li>
    //                     <li>Onion, finely chopped: 1 large or 2 medium</li>
    //                     <li>Garlic, minced: 3 cloves</li>
    //                     <li>Tomato sauce or crushed tomatoes: 24-32 ounces (680-900g)</li>
    //                     <li>Tomato paste: 2 tablespoons (30g) (optional)</li>
    //                     <li>Italian seasoning: 1 tablespoon (15ml)</li>
    //                     <li>Salt: 1 teaspoon (5g), or to taste</li>
    //                     <li>Pepper: 1/2 teaspoon (2.5g), or to taste</li>
    //                     <li>Ricotta cheese: 15 ounces (425g)</li>
    //                     <li>Mozzarella cheese, shredded: 2 cups (200g)</li>
    //                     <li>Parmesan cheese, grated: 1 cup (100g)</li>
    //                     <li>Egg: 1 large</li>
    //                     <li>Fresh basil leaves: a handful for garnish (optional)</li>
    //                     <li>Olive oil: 2 tablespoons (30ml) for sautéing</li>
    //                 </ul>
    //                 </div> {/* Add your left text box here */}
    //                 <div className="text-box-right">
    //                 <h3>PREPARATION METHOD</h3><p>Preheat the Oven: Preheat your oven to 375°F (190°C).
    //                         Cook the Noodles: If you're using regular lasagna noodles, cook them according to the package instructions. If you're using no-boil noodles, you can skip this step.
    //                         Prepare the Meat Sauce: In a large skillet or saucepan, cook the ground beef, turkey, or sausage over medium heat until browned. Add the finely chopped onion and minced garlic, and cook until the onion is translucent and the garlic is fragrant. Stir in the tomato sauce or crushed tomatoes, tomato paste (if using), Italian seasoning, salt, and pepper. Simmer the sauce for about 10-15 minutes, stirring occasionally, to allow the flavors to meld.
    //                         Prepare the Cheese Mixture: In a mixing bowl, combine the ricotta cheese, shredded mozzarella cheese, grated Parmesan cheese, and the egg. Mix well until everything is evenly combined.
    //                         Assemble the Lasagna: In a baking dish, spread a thin layer of the meat sauce on the bottom. Arrange a layer of cooked lasagna noodles over the sauce. Spread a layer of the cheese mixture over the noodles. Repeat the layers of sauce, noodles, and cheese until you've used all of your ingredients, ending with a layer of sauce on top. Sprinkle extra shredded mozzarella and grated Parmesan cheese on top if desired.
    //                         Bake the Lasagna: Cover the baking dish with aluminum foil and bake in the preheated oven for about 25-30 minutes. Then, remove the foil and continue baking for an additional 10-15 minutes, or until the cheese is bubbly and golden brown.
    //                         Let it Rest: Allow the lasagna to cool for a few minutes before slicing and serving. Garnish with fresh basil leaves if desired.
    //                         Serve and Enjoy: Serve the lasagna hot, and enjoy your delicious homemade meal!
    //                 </p></div> {/* Add your right text box here */}
    //             </div>
    //             <button className="close-modal" onClick={toggleModal}>
                    
    //             </button>
    //         </div>
    //     </div>
    // );

