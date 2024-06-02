import React, { useState, useRef, useEffect } from 'react';
import logoPng from "./Photos/logo.png";
import './Navbar.css';



export const Navbar = () => {
    const [menuOpen, setMenuOpen] = useState(false);
    const modalRef = useRef(null);

    const handleClickOutside = (event) => {
        if (modalRef.current && !modalRef.current.contains(event.target)) 
            setMenuOpen(false);   
    };
    useEffect(() => {
        
        document.addEventListener("click", handleClickOutside);
        return () => {
            document.removeEventListener("click", handleClickOutside)
        }
    }, [])

    return (
        <nav className="navbar">
            <div className="logo">
                <img src={logoPng} alt="Logo" />
            </div>
            <div className="mid-section">
                <div className="links">
                    <a href="/">Home</a>
                </div>
            </div>
            <div className="right-section">
                <div className="links informations-links">
                    <a href="#">About us</a>
                    <a href="#">Contact</a>
                </div>
                <div className="hamburger-icon" onClick={() => setMenuOpen(!menuOpen)} ref={modalRef}>
                    &#9776;
                    <div className={`menu-container ${menuOpen ? 'open' : ''}`}>
                        <a href="#0">Recipe</a>
                        <a href="#0">Meal Planning</a>
                        <a href="#0">Shopping List</a>
                        <a href="#0">House hold</a>
                    </div>
                </div>
                <button className="account-button">Account</button>
            </div>
        </nav>
    );
};
