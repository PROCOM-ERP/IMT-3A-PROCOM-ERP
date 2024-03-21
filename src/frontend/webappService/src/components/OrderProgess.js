import React, { useState, useEffect } from 'react';
import Button from './Button.js';
import { useNavigate } from 'react-router-dom';

function OrderProgess({ data, approver, orderId }) {
  const navigate = useNavigate();

  // Function to generate API URL
  const generateApiUrl = (orderId) => {
    return `https://localhost:8041/api/order/v1/orders/${orderId}/progress`;
  };

  // Function to handle API errors
  const handleApiErrors = (response) => {
    if (!response.ok) {
      if (response.status === 401) { navigate("/error401"); }
      else if (response.status === 403) { navigate("/error403"); }
      else { throw new Error(response.status + " " + response.statusText); }
    }
  };

  // Function to validate progress status
  const validateProgressStatus = async () => {
    const apiUrl = generateApiUrl(orderId);
    // Make the API request
    try {
      const response = await fetch(apiUrl, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("Token")}`,
        },
      });
      handleApiErrors(response);
      // If the API request is successful, reload the page
      window.location.reload();
    } catch (error) {
      console.error("API request error: ", error);
    }
  };

  // Handler for modifying progress -> do fetch
  const handleModifyProgress = () => {
    validateProgressStatus();
  };

  return (
    <>
      <div className='title2'>Progress</div>
      {/* Render progress information */}
      {data.map((progress, index) => (
        <div className="information" key={index}>
          <div className="key-container">{progress.name}: </div>
          <div className="value-container">{progress.completed ? 'Done' : 'Ongoing'}</div>
        </div>
      ))}
      {/* Render validate button for approver */}
      {(approver == localStorage.getItem('id')) && (
        <Button onClick={handleModifyProgress}>Validate</Button>
      )}
    </>
  );
}

export default OrderProgess;
