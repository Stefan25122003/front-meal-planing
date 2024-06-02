import { useState } from "react";
import Modal from "react-modal";
import { AddRecipeForm } from "../AddRecipeForm/AddRecipeForm";
import "./AddRecipeButton.css";

export const AddRecipeButton = () => {
  const [isOpen, setIsOpen] = useState();
  Modal.setAppElement("#root");
  const customStyles = {
    overlay: {
      backgroundColor: "rgba(0, 0, 0, 0.75)",
    },
    content: {
      top: "50%",
      left: "50%",
      right: "auto",
      bottom: "auto",
      marginRight: "-50%",
      transform: "translate(-50%, -50%)",
      backgroundColor: "#f2f2f2",
      padding: "20px",
      borderRadius: "10px",
      minWidth: "1000px",
    },
  };
  return (
    <>
      <button type="button" className="add-recipe-button" onClick={() => setIsOpen(true)}>
        Add recipe
      </button>
      <Modal
        isOpen={isOpen}
        onRequestClose={() => setIsOpen(false)}
        contentLabel="Example Modal"
        style={customStyles}
      >
        <AddRecipeForm closeModal={() => setIsOpen(false)} />
      </Modal>
    </>
  );
};
