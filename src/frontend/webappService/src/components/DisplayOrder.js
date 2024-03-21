import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import OrderProgess from './OrderProgess';

function DisplayOrder({ orderId }) {

  const navigate = useNavigate();
  const [order, setOrder] = useState({});
  const [progress, setProgress] = useState([]);
  const [products, setProducts] = useState([]);

  const getOrder = async () => {

    const token = localStorage.getItem("Token");
    const headers = {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    };
    const apiUrl = `https://localhost:8041/api/order/v1/orders/${orderId}`;

    // Make the API request
    await fetch(apiUrl, {
      method: "GET",
      headers: headers,
    })
      .then((response) => {
        if (!response.ok) {
          if (response.status === 401) { navigate("/error401"); }
          else if (response.status === 403) { navigate("/error403"); }
          else { throw new Error(response.status + " " + response.statusText); }
        }
        const res = response.json();
        return res;
      })
      .then((data) => {
        // Extract properties and update state
        const { progress: progressData, products: productsData, ...orderData } = data;
        setProgress(progressData);
        setProducts(productsData);
        setOrder(orderData);
        console.log("[LOG] Order data retrieved");
        console.log(data);
      })
      .catch((error) => {
        console.error("API request error: ", error);
      });
  }

  useEffect(() => {
    getOrder();
  }, []);


  return (
    <>
      <div className='user-form-container'>
        <div className='title'>Order</div>
        <div className='information-container'>
          <OrderProgess data={progress} approver={order.approver} orderId={order.id} />
          <div className='title2'>Information</div>
          {Object.entries(order).map(([key, value]) => (
            <div className="information" key={key}>
              <div className="key-container">{key}</div>
              <div className="value-container">
                <span>{Array.isArray(value) ? value.join(", ") : value}</span>
              </div>
            </div>
          ))}
          <div className='title2'>Products</div>
          {products.map((product, index) => (
            <div className="information" key={index}>
              <div className="key-container">Product {index + 1}</div>
              <div className="value-container">
                <div>Reference: {product.reference}</div>
                <div>Unit Price: {product.unitPrice}</div>
                <div>Quantity: {product.quantity}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </>
  )
}

export default DisplayOrder
