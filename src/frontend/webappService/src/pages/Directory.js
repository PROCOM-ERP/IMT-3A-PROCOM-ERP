import React from 'react'
import Navbar from '../components/Navbar'
import DirectoryTable from '../components/DirectoryTable'

function Directory() {
  return (
    <>
      <Navbar />
      <div className='title'>Directory</div>
      <DirectoryTable />
    </>
  )
}

export default Directory
