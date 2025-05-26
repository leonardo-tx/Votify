import { IoCheckmarkCircleOutline } from "react-icons/io5";

interface ConfirmationModalProps {
  title: string;
  message: string;
  onClose: () => void;
}

export default function ConfirmationModal({
  title,
  message,
  onClose,
}: ConfirmationModalProps) {
  return (
    <div className="fixed inset-0 z-50 bg-black bg-opacity-60 flex items-center justify-center">
      <div className="bg-white rounded-xl shadow-lg p-6 w-full max-w-sm text-center">
        <IoCheckmarkCircleOutline size={48} className="text-green-500 mx-auto mb-4" />
        <h2 className="text-2xl font-bold text-gray-900 mb-2">{title}</h2>
        <p className="text-gray-700">{message}</p>
        <button
          onClick={onClose}
          className="mt-6 w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-md"
        >
          Ok
        </button>
      </div>
    </div>
  );
}
