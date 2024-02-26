import React, { useEffect, useState } from 'react';
import Button from './Button';
import { useNavigate } from 'react-router-dom';

function DisplayOrders() {
  const navigate = useNavigate();
  const userId = localStorage.getItem("id");
  const [userOrders, setUserOrders] = useState({});
  const [managerOrders, setManagerOrders] = useState({});

  const [searchTerm, setSearchTerm] = useState("");
  const [sortBy, setSortBy] = useState(null);

  const handleChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleSort = (key) => {
    setSortBy(key);
  };

  const handleProfil = (id) => {
    // isAdmin ? navigate("/manageUser/" + id) : navigate("/user/" + id);
  };

  let filteredUserOrders = Object.values(userOrders).filter(order => {
    const lowerCaseSearchTerm = searchTerm.toLowerCase();
    return (
      (order.id.toString() && order.id.toString().toLowerCase().includes(lowerCaseSearchTerm)) ||
      (order.createdAt && order.createdAt.toLowerCase().includes(lowerCaseSearchTerm)) ||
      (order.provider && order.provider.toLowerCase().includes(lowerCaseSearchTerm)) ||
      (order.totalAmount && order.totalAmount.toLowerCase().includes(lowerCaseSearchTerm)) ||
      (order.approver && order.approver.toLowerCase().includes(lowerCaseSearchTerm)) ||
      (order.status && order.status.toLowerCase().includes(lowerCaseSearchTerm))
    );
  });

  if (sortBy) {
    filteredUserOrders.sort((a, b) => {
      if (a[sortBy] < b[sortBy]) return -1;
      if (a[sortBy] > b[sortBy]) return 1;
      return 0;
    });
  }

  const tokenName = "Token"; // Need to be the same name as in AuthForm.js components
  const token = localStorage.getItem(tokenName);
  const apiUrl = "https://localhost:8041/api/order/v1/orders?idLoginProfile=" + userId;

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };

  const getOrders = async () => {
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
        setUserOrders(data.ordersByOrderer);
        setManagerOrders(data.ordersByApprover);
        console.log("[LOG] Order list retrieved");
      })
      .catch((error) => {
        console.error("API request error: ", error);
      });
  };

  useEffect(() => {
    getOrders();

  }, []);

  function handleAddOrder() {
    navigate("/addOrder");
  }

  return (
    <>
      <div className='order-container'>
        <div className="line-container">
          <div className="searchbar-container">
            <input className='searchbar'
              type="text"
              placeholder="Search"
              value={searchTerm}
              onChange={handleChange}
            />
          </div>
          <Button onClick={handleAddOrder}>Add Order</Button>
        </div>
        <table className='table-container' >
          <thead className='table-head-container'>
            <tr>
              <th onClick={() => handleSort('id')} >ID</th>
              <th onClick={() => handleSort('provider')} >Provider</th>
              <th onClick={() => handleSort('approver')} >Approver</th>
              <th onClick={() => handleSort('createdAt')} >Created</th>
              <th onClick={() => handleSort('status')} >Status</th>
              <th onClick={() => handleSort('totalAmount')} >Amount</th>
            </tr>
          </thead>

          <tbody className='table-body-container'>
            {filteredUserOrders.map((order, index) => {
              return (
                <tr key={index} onClick={() => handleProfil(order.id)} >
                  <td>{order.id}</td>
                  <td> {order.provider} </td>
                  <td> {order.approver} </td>
                  <td> {order.createdAt} </td>
                  <td> {order.status} </td>
                  <td> {order.totalAmount} </td>
                </tr>
              )
            })}

          </tbody>
        </table>
      </div>
    </>
  )
}

export default DisplayOrders
