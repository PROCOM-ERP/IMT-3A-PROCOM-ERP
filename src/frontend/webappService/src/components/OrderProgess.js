import React, { useState, useEffect } from 'react';
import Button from './Button.js';
import { useNavigate } from 'react-router-dom';

function OrderProgess({ data, approver, orderId }) {
  const navigate = useNavigate();
  const [status, setStatus] = useState(0);

  // Function to get the ID of the first incomplete progress
  const getFirstIncompleteProgressId = () => {
    const firstIncompleteProgress = data.find(progress => !progress.completed);
    return firstIncompleteProgress ? firstIncompleteProgress.id : 0;
  };

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
  const validateProgressStatus = async (statusValue) => {
    console.log("status: ", statusValue);
    const apiUrl = generateApiUrl(orderId);
    // Make the API request
    try {
      const response = await fetch(apiUrl, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("Token")}`,
        },
        body: JSON.stringify({ idProgressStatus: statusValue })
      });
      handleApiErrors(response);
      // If the API request is successful, reload the page
      window.location.reload();
    } catch (error) {
      console.error("API request error: ", error);
    }
  };

  // Effect to validate progress status when status changes
  useEffect(() => {
    if (status !== 0) {
      validateProgressStatus(status);
    }
  }, [status]);

  // Handler for modifying progress
  const handleModifyProgress = () => {
    const progressId = getFirstIncompleteProgressId(); // Get the progress id
    setStatus(progressId); // Set the status state with the progress id
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
