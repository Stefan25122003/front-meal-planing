import React from "react";
import "./pageButtons.css";

export default function PageButtons({ pages, setCurrentPage, currentPage }) {
    const pageNumbers = [];
    pageNumbers.push(0);
    
    if(currentPage > 1){
        pageNumbers.push(currentPage - 1);
    }
    
    if(currentPage !== 0 && currentPage !== pages - 1){
        pageNumbers.push(currentPage)
    }
    
    if(currentPage < pages - 2){
        pageNumbers.push(currentPage + 1);
    }

    pageNumbers.push(pages - 1);


    return (
        <div className="page-buttons">
            {pageNumbers.map(number => (
                <button className={currentPage === number ? "active" : ""} key={number} onClick={() => setCurrentPage(number)}>
                    {number}
                </button>
            ))}
        </div>
    );
}