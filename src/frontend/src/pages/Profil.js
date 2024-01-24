import React from 'react'
import Navbar from '../components/Navbar'

function Profil() {
    const user = {
        id : 1,
        firstname : 'Aina',
        lastname : 'Dirou',
        age : 23,
        job : 'Frontend Developper', 
        office : 'Brest',
        department : 'Software Engineering'
    }

  return (
    <div>
        <Navbar/>
        <h1>Profil</h1>
        <p>Firstname : {user.firstname}</p>
        <p>Lastname : {user.lastname}</p>
        <p>Age : {user.age}</p>
        <p>Office : {user.office}</p>
        <p>Department : {user.department}</p>
        <p>Job : {user.job}</p>
    </div>
  )
}

export default Profil
