import React, { useState, useEffect } from 'react'
import '../css/OrderForm.css'
import { useNavigate } from 'react-router-dom';
import handleFormError from '../utils/handleFormError';
import ErrorForm from '../pages/errors/ErrorForm';

function OrderForm() {
  const navigate = useNavigate();
  const [error, setError] = useState({ title: null, message: null });
  const [gettingError, setGettingError] = useState(false);
  const userId = localStorage.getItem('id');
  const [orderData, setOrderData] = useState({
    provider: '',
    quote: '',
    address: {
      number: '',
      street: '',
      city: '',
      state: '',
      country: '',
      zipcode: '',
      info: ''
    },
    orderer: {
      id: '',
      lastName: '',
      firstName: '',
      email: '',
      phoneNumber: ''
    },
    products: [
      {
        reference: '',
        unitPrice: '',
        quantity: ''
      }
    ]
  });

  const [providers, setProviders] = useState([]);

  const [ordererInfo, setOrdererInfo] = useState({});

  const token = localStorage.getItem('Token');
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };

  function switchEmptyStringToNull(obj) {
    for (const key in obj) {
      if (typeof obj[key] === "object") {
        switchEmptyStringToNull(obj[key]); // Recursively call the function for nested objects
      } else if (obj[key] === "") {
        obj[key] = null; // Switch empty string to null
      }
    }
  }

  const getProviders = async () => {
    const apiUrl = "https://localhost:8041/api/order/v1/providers";
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
        setProviders(data);
        console.log("[LOG] Providers list retrieved");
      })
      .catch((error) => {
        console.error("API request error: ", error);
      });
  }

  const getOrdererInfo = async () => {

    const apiUrl = "https://localhost:8041/api/order/v1/employees/" + userId;
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
        setOrdererInfo(data);
        console.log(data)
        console.log("[LOG] Orderer information retrieved");
      })
      .catch((error) => {
        console.error("API request error: ", error);
      });
  }

  useEffect(() => {
    getProviders();
  }, []);

  useEffect(() => {
    getOrdererInfo();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setOrderData(prevData => ({
      ...prevData,
      [name]: value
    }));
  };

  const handleAddressChange = (e) => {
    const { name, value } = e.target;
    setOrderData(prevData => ({
      ...prevData,
      address: {
        ...prevData.address,
        [name]: value
      }
    }));
  };

  const handleOrdererChange = (e) => {
    const { name, value } = e.target;
    setOrderData(prevData => ({
      ...prevData,
      orderer: {
        ...prevData.orderer,
        [name]: value
      }
    }));
  };

  const handleProductChange = (e, index) => {
    const { name, value } = e.target;
    const updatedProducts = [...orderData.products];
    updatedProducts[index][name] = value;
    setOrderData(prevData => ({
      ...prevData,
      products: updatedProducts
    }));
  };

  const handleAddProduct = () => {
    setOrderData(prevData => ({
      ...prevData,
      products: [...prevData.products, { reference: '', unitPrice: '', quantity: '' }]
    }));
  };

  function dataProcessing() {
    // Check if orderer fields are empty
    const isOrdererEmpty = Object.values(orderData.orderer).some(value => value === "");
    // Check if address fields are empty
    const isAddressEmpty = Object.values(orderData.address).some(value => value === "");
    if (isAddressEmpty || isOrdererEmpty) {
      // Check if orderer fields are empty individually
      const isIdEmpty = orderData.orderer.id === "";
      const isLastNameEmpty = orderData.orderer.lastName === "";
      const isFirstNameEmpty = orderData.orderer.firstName === "";
      const isEmailEmpty = orderData.orderer.email === "";
      const isPhoneNumberEmpty = orderData.orderer.phoneNumber === "";
      // Check if address fields are empty individually
      const isNumberEmpty = orderData.address.number === "";
      const isStreetEmpty = orderData.address.street === "";
      const isCityEmpty = orderData.address.city === "";
      const isStateEmpty = orderData.address.state === "";
      const isCountryEmpty = orderData.address.country === "";
      const isZipcodeEmpty = orderData.address.zipcode === "";
      const isInfoEmpty = orderData.address.info === "";
      // If orderer fields are empty, replace them with ordererInfo
      const updatedOrderData = {
        ...orderData,
        // Replace orderer fields with ordererInfo if empty
        orderer: {
          ...orderData.orderer,
          // Update fields only if they are empty
          ...(isIdEmpty && { id: ordererInfo.id || userId }),
          ...(isLastNameEmpty && { lastName: ordererInfo.lastName }),
          ...(isFirstNameEmpty && { firstName: ordererInfo.firstName }),
          ...(isEmailEmpty && { email: ordererInfo.email }),
          ...(isPhoneNumberEmpty && { phoneNumber: ordererInfo.phoneNumber })
        },
        // Replace address fields with ordererInfo.address if empty
        address: {
          ...orderData.address,
          // Update fields only if they are empty
          ...(isNumberEmpty && { number: ordererInfo.address.number }),
          ...(isStreetEmpty && { street: ordererInfo.address.street }),
          ...(isCityEmpty && { city: ordererInfo.address.city }),
          ...(isStateEmpty && { state: ordererInfo.address.state }),
          ...(isCountryEmpty && { country: ordererInfo.address.country }),
          ...(isZipcodeEmpty && { zipcode: ordererInfo.address.zipcode }),
          ...(isInfoEmpty && { info: ordererInfo.address.info })
        }
      };
      switchEmptyStringToNull(updatedOrderData);
      // Convert property types to make it correspond to JSON format needed for sending
      updatedOrderData.provider = parseInt(updatedOrderData.provider); // Convert provider to number
      updatedOrderData.products.forEach(product => {
        product.unitPrice = parseInt(product.unitPrice); // Convert unitPrice to number
        product.quantity = parseInt(product.quantity); // Convert quantity to number
      });
      return updatedOrderData;
    } else {
      setOrderData(prevData => ({
        ...prevData,
        orderer: { ...prevData.orderer, id: userId }
      }));
      // Convert property types to make it correspond to JSON format needed for sending
      orderData.provider = parseInt(orderData.provider); // Convert provider to number
      orderData.products.forEach(product => {
        product.unitPrice = parseInt(product.unitPrice); // Convert unitPrice to number
        product.quantity = parseInt(product.quantity); // Convert quantity to number
      });
      switchEmptyStringToNull(orderData);
      return orderData;
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault();

    const dataToSend = dataProcessing();

    // API URL
    const apiUrl = "https://localhost:8041/api/order/v1/orders";

    // Make the API request
    fetch(apiUrl, {
      method: "POST",
      credentials: "include",
      headers: headers,
      body: JSON.stringify(dataToSend)
    })
      .then(async (response) => {
        const [getError, error] = await handleFormError(response, navigate);
        if (getError) {
          setGettingError(true);
          setError(error);
        } else {
          console.log("[LOG] Order added with success");
          navigate("/orderManagement");
        }
      })
      .catch(error => {
        console.error('Error adding order:', error);
      });
  };

  return (
    <>
      <form className='order-form-container' onSubmit={handleSubmit}>
        <div>
          {/* Provider */}
          <div className='order-line-container'>
            <label htmlFor="provider">Provider:</label>
            <select id="provider" name="provider" value={orderData.provider} onChange={handleChange}>
              <option value="">Select Provider</option>
              {providers.map(provider => (
                <option key={provider.id} value={provider.id}>{provider.name}</option>
              ))}
            </select>
          </div>

          {/* Quote */}
          <div className='order-line-container'>
            <label htmlFor="quote">Quote:</label>
            <input type="text" id="quote" name="quote" value={orderData.quote} onChange={handleChange} />
          </div>
        </div>

        {/* Products */}
        <div className='title2'>Products</div>
        <div>
          {
            orderData.products.map((product, index) => (
              <div key={index}>
                <p>Product {index + 1}</p>
                <div className='order-line-container'>
                  <label htmlFor={`productReference${index}`}>Reference:</label>
                  <input type="text" id={`productReference${index}`} name="reference" value={product.reference} onChange={(e) => handleProductChange(e, index)} />
                </div>
                <div className='order-line-container'>
                  <label htmlFor={`productUnitPrice${index}`}>Unit Price:</label>
                  <input type="text" id={`productUnitPrice${index}`} name="unitPrice" value={product.unitPrice} onChange={(e) => handleProductChange(e, index)} />
                </div>
                <div className='order-line-container'>
                  <label htmlFor={`productQuantity${index}`}>Quantity:</label>
                  <input type="text" id={`productQuantity${index}`} name="quantity" value={product.quantity} onChange={(e) => handleProductChange(e, index)} />
                </div>
              </div>
            ))
          }
        </div>
        <button type="button" onClick={handleAddProduct}>Add Product</button>

        {/* Orderer */}
        <div className='title2'>Orderer</div>
        <div>
          <div className='order-line-container'>
            <label htmlFor="lastName">Last Name:</label>
            <input type="text" id="lastName" name="lastName" value={orderData.orderer.lastName} placeholder={ordererInfo.lastName} onChange={handleOrdererChange} />
          </div>
          <div className='order-line-container'>
            <label htmlFor="firstName">First Name:</label>
            <input type="text" id="firstName" name="firstName" value={orderData.orderer.firstName} placeholder={ordererInfo.firstName} onChange={handleOrdererChange} />
          </div>
          <div className='order-line-container'>
            <label htmlFor="email">Email:</label>
            <input type="text" id="email" name="email" value={orderData.orderer.email} placeholder={ordererInfo.email} onChange={handleOrdererChange} />
          </div>
          <div className='order-line-container'>
            <label htmlFor="phoneNumber">Phone Number:</label>
            <input type="text" id="phoneNumber" name="phoneNumber" value={orderData.orderer.phoneNumber} placeholder={ordererInfo.phoneNumber} onChange={handleOrdererChange} />
          </div>
        </div>

        {/* Address */}
        <div className='title2'>Address</div>
        <div>
          <div>
            {/* <p>Name: {ordererInfo.address.number} {ordererInfo.address.city}</p> */}
          </div>
          <div className='order-line-container'>
            <label htmlFor="number">Number:</label>
            <input type="text" id="number" name="number" value={orderData.address.number} placeholder={ordererInfo.address ? ordererInfo.address.number : ''} onChange={handleAddressChange} />
          </div>
          <div className='order-line-container'>
            <label htmlFor="street">Street:</label>
            <input type="text" id="street" name="street" value={orderData.address.street} placeholder={ordererInfo.address ? ordererInfo.address.street : ''} onChange={handleAddressChange} />
          </div>
          <div className='order-line-container'>
            <label htmlFor="city">City:</label>
            <input type="text" id="city" name="city" value={orderData.address.city} placeholder={ordererInfo.address ? ordererInfo.address.city : ''} onChange={handleAddressChange} />
          </div>
          <div className='order-line-container'>
            <label htmlFor="state">State:</label>
            <input type="text" id="state" name="state" value={orderData.address.state} placeholder={ordererInfo.address ? ordererInfo.address.state : ''} onChange={handleAddressChange} />
          </div>
          <div className='order-line-container'>
            <label htmlFor="country">Country:</label>
            <input type="text" id="country" name="country" value={orderData.address.country} placeholder={ordererInfo.address ? ordererInfo.address.country : ''} onChange={handleAddressChange} />
          </div>
          <div className='order-line-container'>
            <label htmlFor="zipcode">Zip code:</label>
            <input type="text" id="zipcode" name="zipcode" value={orderData.address.zipcode} placeholder={ordererInfo.address ? ordererInfo.address.zipcode : ''} onChange={handleAddressChange} />
          </div>
          <div className='order-line-container'>
            <label htmlFor="info">Information:</label>
            <input type="text" id="info" name="info" value={orderData.address.info} placeholder={ordererInfo.address ? ordererInfo.address.info : ''} onChange={handleAddressChange} />
          </div>
        </div >
        <button type="submit">Submit</button>
      </form >

      {gettingError && (
        <ErrorForm
          title={error.title}
          message={error.message}
          onClose={() => {
            setGettingError(false);
          }}
        />
      )}

    </>
  );
}

export default OrderForm
