import React from 'react';
import data from "../data/data.json";

function DirectoryTable() {
    const users = data.users;

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
