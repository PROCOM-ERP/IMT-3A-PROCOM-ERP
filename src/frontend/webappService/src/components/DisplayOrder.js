import React, { useEffect, useState } from 'react'

function DisplayOrder({ orderId }) {

  const [order, setOrder] = useState({});

  const getOrder = async () => {

    const token = localStorage.getItem("Token");
    const headers = {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    };
    const apiUrl = `https://localhost:8041/api/order/v1/order/${orderId}`;

    // Make the API request
    // await fetch(apiUrl, {
    //   method: "GET",
    //   headers: headers,
    // })
    //   .then((response) => {
    //     if (!response.ok) throw new Error(response.status);
    //     const res = response.json();
    //     return res;
    //   })
    //   .then((data) => {
    //     setOrder(data);
    //     console.log("[LOG] Order data retrieved");
    //   })
    //   .catch((error) => {
    //     console.error("API request error: ", error);
    //   });
  }

  useEffect(() => {
    getOrder();
  }, []);

  return (
    <>
      {Object.entries(order).map(([key, value]) => {
        <p>{key}: {value}</p>
      })}
    </>
  )
}

export default DisplayOrder
