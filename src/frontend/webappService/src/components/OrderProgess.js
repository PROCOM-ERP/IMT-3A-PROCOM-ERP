import React from 'react'
import Button from './Button.js'

function OrderProgess({ data }) {
  const handleModifyProgress = () => {

  }

  return (
    <>
      <div className='title2'>Progress</div>
      {
        data.map((progress, index) => (
          <div className="information" key={index}>
            <div className="key-container">{progress.name}: </div>
            <div className="value-container">{progress.completed ? 'Done' : 'Ongoing'}</div>
          </div>
        ))

      }
      <Button onClick={handleModifyProgress}>Modify</Button>
    </>
  )
}

export default OrderProgess
