import React, { useState, useEffect } from 'react';
import Cards from './Cards.jsx';
import PageButtons from "./pageButons";

function RecipeCards() {
  const [data, setData] = useState(null);

  const [pages, setPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(0);//java paage starts from 0

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch('http://localhost:5000/api/v1/recipes/recipePage?pageNo=' + currentPage );
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const json = await response.json();
        setData(Array.isArray(json.content) ? json.content : [json.content]);

        setPages(json.totalPages);
      } catch (error) {
        console.error('Error:', error);
      }
    };

    fetchData();
  }, [currentPage]);

  return (
    <>
      <div className='cards-container'>
      {data ? data.slice(1, 30).map((recipe) => <Cards key={recipe.recipeId} recipe={recipe} />) : <div>Loading...</div>}
      </div>
      
      <PageButtons pages={pages}  setCurrentPage={setCurrentPage} currentPage={currentPage}/>
    </>
  );
}

export default RecipeCards;
//ok