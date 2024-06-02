import React, { useState, useEffect } from 'react';
import './Cards.css';
import Modal from './modal';
import {fetchImage} from './pexelsApiFetch.js';


function Cards({ recipe }) {
  const [modalOpen, setModalOpen] = useState(false);
  const [imageSrc, setImageSrc] = useState(null);

  const toggleModal = () => {
    setModalOpen(!modalOpen);
  };

  useEffect(() => {
    if (recipe.imageList && recipe.imageList.length > 0) {
      setImageSrc(recipe.imageList[0]);
    } else {
      fetchImage(recipe.recipeTitle.includes("recipe") ? recipe.recipeTitle : recipe.recipeTitle + 'recipe')
        .then((url) => {
          setImageSrc(url);
        });
    }
  }, [recipe]);

  useEffect(() => {
    if (modalOpen) {
      document.body.classList.add('no-scroll');
    } else {
      document.body.classList.remove('no-scroll');
    }
  }, [modalOpen]);

  


  return(
    <div className="card">
      <img src={imageSrc} alt={recipe.recipeTitle} className="card-img"/>
      <div className="card-body">
        <h5 className="card-title">{recipe.recipeTitle}</h5>
        <button className="btn btn-primary" onClick={toggleModal}>Details</button>
      </div>
      {modalOpen && <Modal recipe={recipe} toggleModal={toggleModal} img ={imageSrc} />}
    </div>
  );
}

export default Cards;