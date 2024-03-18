import React, { useState } from 'react'
import Button from './Button.js'
import { useNavigate } from 'react-router-dom';

function OrderProgess({ data, approver, idOrder }) {
  const navigate = useNavigate();
  const [status, setStatus] = useState(0);

  const getFirstIncompleteProgressId = () => {
    const firstIncompleteProgress = data.find(progress => !progress.completed);
    return firstIncompleteProgress ? firstIncompleteProgress.id : 0;
  };

  const validateProgressStatus = async () => {
    const apiUrl = `https://localhost:8041/api/order/v1/orders/${idOrder}/progress`;
    // Make the API request
    try {
      const response = await fetch(apiUrl, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("Token")}`,
        },
        body: JSON.stringify({ idProgressStatus: status })
      });
      if (!response.ok) {
        if (response.status === 401) { navigate("/error401"); }
        else if (response.status === 403) { navigate("/error403"); }
        else { throw new Error(response.status + " " + response.statusText); }
      }
    } catch (error) {
      console.error("API request error: ", error);
    }
  }

  const handleModifyProgress = () => {
    setStatus(getFirstIncompleteProgressId);
    validateProgressStatus();
  }

  return (
    <>
      <div className='title2'>Progress</div>
      {
        data.map((progress, index) => (
          <div className="information" key={index}>
            <div className="key-container">{progress.name}: </div>
            <div className="value-container">{progress.completed ? 'Done' : 'Ongoing'}</div>
          </div>
        ))

      }
      {(approver == localStorage.getItem('id')) && (
        <Button onClick={handleModifyProgress}>Validate</Button>
      )}
    </>
  )
}

export default OrderProgess
