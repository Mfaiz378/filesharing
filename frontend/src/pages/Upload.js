
import { useState } from "react";
import axios from "axios";

function Upload() {
  const [file, setFile] = useState(null);

  const handleUpload = async () => {
    const formData = new FormData();
    formData.append("file", file);

    try {
      await axios.post("http://localhost:8080/api/files/upload", formData);
      alert("File uploaded");
    } catch (err) {
      alert("Upload failed");
    }
  };

  return (
    <div>
      <h2>Upload File</h2>
      <input type="file" onChange={e => setFile(e.target.files[0])} />
      <button onClick={handleUpload}>Upload</button>
    </div>
  );
}
export default Upload;
