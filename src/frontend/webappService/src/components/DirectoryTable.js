import React, { useState ,useEffect } from 'react';
import data from "../data/data.json";

function DirectoryTable() {
    //const defaultUsers = data.users;
    const [users, setUsers] = useState([]);

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
                setUsers(data);
                console.info("[DATA] " + JSON.stringify(data));
                console.log("[LOG] profil info retrieve");
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
        <table>
            <thead>
                <th>Name</th>
                <th>Office</th>
                <th>Department</th>
                <th>Job</th>
            </thead>
            <tbody>
                {console.log(users)}
                {users.map((user, index) => {
                    return(
                        <tr key={index}>
                            <td> {user.firstname + ' ' + user.lastname} </td>
                            <td> {user.office} </td>
                            <td> {user.department} </td>
                            <td> {user.job} </td>
                        </tr>
                    )
                    })}
                
            </tbody>
        </table>
        </>
    )
}

export default DirectoryTable
