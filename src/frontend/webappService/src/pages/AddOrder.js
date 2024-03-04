import React, { useState, useEffect } from 'react'

export default function AddOrder() {
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

  const [providers, setProviders] = useState({});

  const [ordererInfo, setOrdererInfo] = useState({});

  const getProviders = async () => {
    const apiUrl = "https://localhost:8041/api/order/v1/providers";
    // Make the API request
    await fetch(apiUrl, {
      method: "GET",
      headers: headers,
    })
      .then((response) => {
        if (!response.ok) throw new Error(response.status);
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
    const idEmployee = localStorage.getItem('id');
    const apiUrl = "https://localhost:8041/api/order/v1/employees/" + idEmployee;
    // Make the API request
    await fetch(apiUrl, {
      method: "GET",
      headers: headers,
    })
      .then((response) => {
        if (!response.ok) throw new Error(response.status);
        const res = response.json();
        return res;
      })
      .then((data) => {
        setOrdererInfo(data);
        console.log("[LOG] Orderer information retrieved");
      })
      .catch((error) => {
        console.error("API request error: ", error);
      });
  }

  useEffect(() => {
    getProviders();
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

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: Handle form submission, e.g., send data to backend
    console.log(orderData);
  };

  return (
    <div>
      <h2>Add User Order</h2>
      <form onSubmit={handleSubmit}>
        {/* Provider */}
        <div>
          <label htmlFor="provider">Provider:</label>
          <select id="provider" name="provider" value={orderData.provider} onChange={handleChange}>
            <option value="">Select Provider</option>
            {providers.map(provider => (
              <option key={provider.id} value={provider.id}>{provider.name}</option>
            ))}
          </select>
        </div>

        {/* Quote */}
        <div>
          <label htmlFor="quote">Quote:</label>
          <input type="text" id="quote" name="quote" value={orderData.quote} onChange={handleChange} />
        </div>

        {/* Address */}
        <h3>Address</h3>
        <div>
          <label htmlFor="number">Number:</label>
          <input type="number" id="number" name="number" value={orderData.address.number} placeholder={ordererInfo.address.number} onChange={handleAddressChange} />
          <label htmlFor="street">Street:</label>
          <input type="text" id="street" name="street" value={orderData.address.street} placeholder={ordererInfo.address.street} onChange={handleAddressChange} />
          <label htmlFor="city">City:</label>
          <input type="text" id="city" name="city" value={orderData.address.city} placeholder={ordererInfo.address.city} onChange={handleAddressChange} />
          <label htmlFor="state">State:</label>
          <input type="text" id="state" name="state" value={orderData.address.state} placeholder={ordererInfo.address.state} onChange={handleAddressChange} />
          <label htmlFor="country">Country:</label>
          <input type="text" id="country" name="country" value={orderData.address.country} placeholder={ordererInfo.address.country} onChange={handleAddressChange} />
          <label htmlFor="zipcode">Zip code:</label>
          <input type="text" id="zipcode" name="zipcode" value={orderData.address.zipcode} placeholder={ordererInfo.address.zipcode} onChange={handleAddressChange} />
          <label htmlFor="info">Information:</label>
          <input type="text" id="info" name="info" value={orderData.address.info} placeholder={ordererInfo.address.info} onChange={handleAddressChange} />
        </div>

        {/* Orderer */}
        <h3>Orderer</h3>
        <div>
          <label htmlFor="lastName">Last Name:</label>
          <input type="text" id="lastName" name="lastName" value={orderData.orderer.lastName} onChange={handleOrdererChange} />
          <label htmlFor="firstName">First Name:</label>
          <input type="text" id="firstName" name="firstName" value={orderData.orderer.firstName} onChange={handleOrdererChange} />
          <label htmlFor="email">Email:</label>
          <input type="text" id="email" name="email" value={orderData.orderer.email} onChange={handleOrdererChange} />
          <label htmlFor="phoneNumber">Phone Number:</label>
          <input type="text" id="phoneNumber" name="phoneNumber" value={orderData.orderer.phoneNumber} onChange={handleOrdererChange} />
          {/* Other orderer fields */}
          {/* ... */}
        </div>

        {/* Products */}
        <h3>Products</h3>
        {orderData.products.map((product, index) => (
          <div key={index}>
            <label>Product {index + 1}</label>
            <div>
              <label htmlFor={`productReference${index}`}>Reference:</label>
              <input type="text" id={`productReference${index}`} name="reference" value={product.reference} onChange={(e) => handleProductChange(e, index)} />
            </div>
            <div>
              <label htmlFor={`productUnitPrice${index}`}>Unit Price:</label>
              <input type="text" id={`productUnitPrice${index}`} name="unitPrice" value={product.unitPrice} onChange={(e) => handleProductChange(e, index)} />
            </div>
            <div>
              <label htmlFor={`productQuantity${index}`}>Quantity:</label>
              <input type="text" id={`productQuantity${index}`} name="quantity" value={product.quantity} onChange={(e) => handleProductChange(e, index)} />
            </div>
          </div>
        ))}
        <button type="button" onClick={handleAddProduct}>Add Product</button>

        <button type="submit">Submit</button>
      </form>
    </div>
  );
}



