import React from 'react'
import Navbar from '../components/Navbar'
import DirectoryTable from '../components/DirectoryTable'
import CheckUserConnection from '../components/CheckUserConnection'

function Directory() {
  return (
    <>
      <CheckUserConnection />
      <Navbar />
      <div className='title'>Directory</div>
      <DirectoryTable />
    </>
  )
}

export default Directory
