import React, { useState, useEffect } from 'react';
import data from "../data/data.json";
import { Link } from 'react-router-dom';

function DirectoryTable() {
    //const defaultUsers = data.users;
    const userId = localStorage.getItem("id");
    const [users, setUsers] = useState([]);

    const [searchTerm, setSearchTerm] = useState('');
    const [sortBy, setSortBy] = useState(null);

    const handleChange = (e) => {
        setSearchTerm(e.target.value);
    };

    const handleSort = (key) => {
        setSortBy(key);
    };

    // TODO : tester si la valeur est null -> ne pas appliquer le filtre
    let filteredUsers = users.filter(user =>
        user.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.lastName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(searchTerm.toLowerCase())
        //user.phoneNumber.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (sortBy) {
        filteredUsers.sort((a, b) => {
            if (a[sortBy] < b[sortBy]) return -1;
            if (a[sortBy] > b[sortBy]) return 1;
            return 0;
        });
    }

    const tokenName = "Token"; // Need to be the same name as in AuthForm.js components
    const token = localStorage.getItem(tokenName);
    const apiUrl = "https://localhost:8041/api/dir/v1/employees";

    const headers = {
        'Authorization': `Bearer ${token}`,
    };

    const getEmployees = async () => {

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
            .then(data => {
                setUsers(data.filter(user => user.id !== userId));
                console.log("[LOG] Users profil information retrieved");
            })
            .catch(error => {
                console.error('API request error: ', error);
            });
    }

    useEffect(() => {
        getEmployees();
    }, []);

    return (
        <>
            <input
                type="text"
                placeholder="Search"
                value={searchTerm}
                onChange={handleChange}
            />
            <table>
                <thead>
                    <th onClick={() => handleSort('id')} >ID</th>
                    <th onClick={() => handleSort('firstName')} >Firstname</th>
                    <th onClick={() => handleSort('lastName')} >Lastname</th>
                    <th onClick={() => handleSort('email')} >Email</th>
                    <th onClick={() => handleSort('phoneNumber')} >Phone number</th>
                </thead>
                <tbody>
                    {filteredUsers.map((user, index) => {
                        return (
                            <tr key={index} >
                                <td> <Link to={`/user/${user.id}`}>{user.id} </Link> </td>
                                <td> {user.firstName} </td>
                                <td> {user.lastName} </td>
                                <td> {user.email} </td>
                                <td> {user.phoneNumber} </td>
                            </tr>
                        )
                    })}

                </tbody>
            </table>
        </>
    )
}

export default DirectoryTable
