import React, { useState, useRef,useEffect } from 'react';
import './AIModal.css';
import Modal from './modal.js';

function AIModal({ onClose }) {
    const [loading, setLoading] = useState(false);
    const [isBlurred, setIsBlurred] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [recipe, setRecipe] = useState(null);
    const [recipeModalOpen, setRecipeModalOpen] = useState(false);

    const toggleModal = () => {
        setRecipeModalOpen(!recipeModalOpen);
    }

    useEffect(() => {
        if (recipeModalOpen) {
          document.body.classList.add('no-scroll');

        } else {
          document.body.classList.remove('no-scroll');
        }
    }, [recipeModalOpen]);






    const recipeNameRef = useRef(null);
    const mainIngredientsRef = useRef(null);
    const textInputRef = useRef(null);

    const validateIngredients = (ingredients) => {
        if (ingredients.trim() === "") return true; // Empty string is valid
        const regex = /^(\w+|\w+(,\s\w+)*\s?)$/; // Single word or words separated by ", "
        return regex.test(ingredients);
    };

    const handleSend = async () => {
        const recipeName = recipeNameRef.current.value;
        const mainIngredients = mainIngredientsRef.current.value;
        const textInput = textInputRef.current.value;

        // Determine the recipe format
        const recipeFormat = textInput ? 'text' : 'youtube';

        /// Validation code
        // Check if there's text in both textarea sets
        
        if ((recipeName || mainIngredients) && textInput) {
            setErrorMessage('Please fill either the YouTube input fields or the text input field, not both.');
            return;
        }

        // Validate main ingredients format
        if (!validateIngredients(mainIngredients)) {
            setErrorMessage('Please enter the main ingredients in a valid format (single word or words separated by ", ").');
            return;
        }

        // Check if mainIngredients is valid and recipeName is empty
        if (mainIngredients && !recipeName) {
            setErrorMessage('Please enter a recipe name if you have entered main ingredients.');
            return;
        }

        setLoading(true);
        setIsBlurred(true);
        setErrorMessage(''); // Clear any previous error message
        
        // Send data to the server (POST request)
        try {
            //console.log(recipeFormat, recipeName, mainIngredients, textInput);
            
            let response;
            let data;
            let apiUrl;

            if (recipeFormat === 'text') {
                apiUrl = `http://localhost:5000/api/v1/recipes/RecipeAI?recipeFormat=${encodeURIComponent(recipeFormat)}&input=${encodeURIComponent(textInput)}&mainIngredients=`;
            } else {
                apiUrl = `http://localhost:5000/api/v1/recipes/RecipeAI?recipeFormat=${encodeURIComponent(recipeFormat)}&input=${encodeURIComponent(recipeName)}&mainIngredients=${encodeURIComponent(mainIngredients)}`;
            }
            
            //console.log(apiUrl);
            
            response = await fetch(apiUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
            });
            
            data = await response.json();
            //console.log(data);
            
            if (response.ok) {
                //console.log('Recipe generated successfully:', data);
                setRecipe(data);
                setRecipeModalOpen(true);
            }
            else 
                setErrorMessage('An error occurred while generating the recipe.');
        } catch (error) {
            console.error('Error:', error);
            setErrorMessage('An error occurred while generating the recipe.');
        }

        // TODO: Fix loading for recipe and showing the recipe correctly
        
        await new Promise(resolve => setTimeout(resolve, 2000));

        setLoading(false);
        setIsBlurred(false);
    };

    return (
        <>
            <div className={`modal AI ${isBlurred ? 'blur' : ''} ${recipeModalOpen ? 'hidden' : ''}`}>
                <div onClick={onClose} className="overlay"></div>
                <div className="modal-content">
                    <button className="close-modal" onClick={onClose}></button>
                    <h2>Recipe AI Generator</h2>
                    <h3 className="additional-text">Generate from youtube</h3>
                    <div className="textarea-container">
                        <div>
                            <p className="textarea-label">Recipe name:</p>
                            <textarea ref={recipeNameRef} className="recipe-textarea-first" placeholder="Enter recipe name here..."></textarea>
                        </div>
                        <div>
                            <p className="textarea-label">Main ingredients:</p>
                            <textarea ref={mainIngredientsRef} className="recipe-textarea-first-ingredients" placeholder="(optional)Enter ingredient1, ingredient2..."></textarea>
                        </div>
                    </div>
                    <h3 className="additional-text">Generate from text input</h3>
                    <div className="textarea-container">
                        <div>
                            <textarea ref={textInputRef} className="recipe-textarea-second" placeholder="Enter recipe in text format(simple text, xml, html, etc...)"></textarea>
                        </div>
                    </div>
                    {errorMessage && <p className="error-message">{errorMessage}</p>}
                    {!loading && <button className="send-button" onClick={handleSend}>Send</button>}
                    {loading && (
                        <div className={`loader ${isBlurred ? 'no-blur' : ''}`}></div>
                    )}
                </div>
            </div>

            {recipeModalOpen && <Modal recipe ={recipe} toggleModal={toggleModal}  />}

        </>
    );
}

export default AIModal;