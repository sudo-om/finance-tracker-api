import React, { useEffect, useState } from 'react';
import { Plus, Trash2, Search, ArrowDownCircle, ArrowUpCircle, X } from 'lucide-react';
import { api } from '../api';

export default function TransactionsView() {
  const [activeTab, setActiveTab] = useState('expenses'); // 'expenses' | 'incomes'
  const [expenses, setExpenses] = useState([]);
  const [incomes, setIncomes] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState('expense'); // 'expense' | 'income'
  const [error, setError] = useState('');

  // Form State
  const [formData, setFormData] = useState({
    title: '',
    merchant: '',
    source: '',
    amount: '',
    expenseDate: new Date().toISOString().split('T')[0],
    incomeDate: new Date().toISOString().split('T')[0],
    paymentMethod: 'UPI',
    categoryId: '',
    description: '',
  });

  useEffect(() => {
    fetchTransactions();
    fetchCategories();
  }, [activeTab]);

  const fetchCategories = async () => {
    try {
      const res = await api.getCategories();
      const catList = Array.isArray(res) ? res : (res?.data || []);
      setCategories(catList);
    } catch (err) {
      console.error('Error fetching categories:', err);
    }
  };

  const openAddModal = (type) => {
    setModalType(type);
    setError('');
    const targetType = type === 'expense' ? 'EXPENSE' : 'INCOME';
    const available = categories.filter((c) => (c.type || '').toUpperCase() === targetType);
    setFormData((prev) => ({
      ...prev,
      categoryId: available.length > 0 ? available[0].id : (categories[0]?.id || ''),
    }));
    setShowModal(true);
  };

  const fetchTransactions = async () => {
    setLoading(true);
    try {
      if (activeTab === 'expenses') {
        const res = await api.getExpenses();
        const list = Array.isArray(res) ? res : (res?.content || []);
        setExpenses(list);
      } else {
        const res = await api.getIncomes();
        const list = Array.isArray(res) ? res : (res?.content || []);
        setIncomes(list);
      }
    } catch (err) {
      console.error('Error fetching transactions:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this item?')) return;
    try {
      if (activeTab === 'expenses') {
        await api.deleteExpense(id);
      } else {
        await api.deleteIncome(id);
      }
      fetchTransactions();
    } catch (err) {
      alert(err.message || 'Delete failed');
    }
  };

  const handleAddSubmit = async (e) => {
    e.preventDefault();
    setError('');

    const targetCategory = categories.find((c) => c.id === formData.categoryId) || categories[0];
    const catId = targetCategory ? targetCategory.id : formData.categoryId;

    if (!catId) {
      setError('Please select a valid category');
      return;
    }

    try {
      if (modalType === 'expense') {
        await api.createExpense({
          title: formData.merchant || formData.title,
          merchant: formData.merchant || formData.title,
          amount: parseFloat(formData.amount),
          expenseDate: formData.expenseDate,
          paymentMethod: formData.paymentMethod,
          categoryId: catId,
          description: formData.description || 'Web transaction',
        });
      } else {
        await api.createIncome({
          source: formData.source || formData.title,
          amount: parseFloat(formData.amount),
          incomeDate: formData.incomeDate,
          categoryId: catId,
          description: formData.description || 'Web transaction',
        });
      }

      setShowModal(false);
      fetchTransactions();
    } catch (err) {
      setError(err.message || 'Failed to record transaction');
    }
  };

  const filteredItems = (activeTab === 'expenses' ? expenses : incomes).filter((item) => {
    const text = (item.merchant || item.title || item.source || '').toLowerCase();
    return text.includes(search.toLowerCase());
  });

  const availableCategories = categories.filter(
    (c) => (c.type || '').toUpperCase() === (modalType === 'expense' ? 'EXPENSE' : 'INCOME')
  );

  return (
    <div className="space-y-6">
      {/* Header & Tabs */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black uppercase tracking-tight text-black">Transactions Manager</h1>
          <p className="text-sm font-bold text-gray-700">View and manage your income & expenses</p>
        </div>

        <div className="flex items-center gap-3">
          <button onClick={() => openAddModal('expense')} className="nb-btn nb-btn-danger">
            <Plus className="w-4 h-4 stroke-[3]" /> Add Expense
          </button>
          <button onClick={() => openAddModal('income')} className="nb-btn nb-btn-success">
            <Plus className="w-4 h-4 stroke-[3]" /> Add Income
          </button>
        </div>
      </div>

      {/* Main Container */}
      <div className="nb-card space-y-6">
        
        {/* Top Controls: Type Switcher & Search Bar */}
        <div className="flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex border-2 border-black rounded-lg p-1 bg-[#F4F0EA] w-full md:w-auto">
            <button
              onClick={() => setActiveTab('expenses')}
              className={`flex-1 md:flex-none px-6 py-2 font-black uppercase text-xs rounded transition-all flex items-center justify-center gap-2 ${
                activeTab === 'expenses' ? 'bg-[#FF2A85] text-white border-2 border-black nb-shadow-sm' : 'text-black'
              }`}
            >
              <ArrowDownCircle className="w-4 h-4" /> Expenses
            </button>
            <button
              onClick={() => setActiveTab('incomes')}
              className={`flex-1 md:flex-none px-6 py-2 font-black uppercase text-xs rounded transition-all flex items-center justify-center gap-2 ${
                activeTab === 'incomes' ? 'bg-[#00E5FF] text-black border-2 border-black nb-shadow-sm' : 'text-black'
              }`}
            >
              <ArrowUpCircle className="w-4 h-4" /> Incomes
            </button>
          </div>

          <div className="relative w-full md:w-72">
            <Search className="w-4 h-4 absolute left-3 top-3.5 text-black" />
            <input
              type="text"
              className="nb-input pl-9 text-sm"
              placeholder={`Search ${activeTab}...`}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </div>

        {/* Transactions Table */}
        {filteredItems.length === 0 ? (
          <div className="text-center py-12 border-2 border-dashed border-black rounded bg-[#F4F0EA]">
            <p className="text-base font-black uppercase text-gray-700">No {activeTab} records found.</p>
            <p className="text-xs font-bold text-gray-500 mt-1">Click the Add button above to create one.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-[#FFE600] border-2 border-black text-xs font-black uppercase">
                  <th className="p-3 border-r-2 border-black">Date</th>
                  <th className="p-3 border-r-2 border-black">{activeTab === 'expenses' ? 'Merchant / Title' : 'Source'}</th>
                  <th className="p-3 border-r-2 border-black">Category</th>
                  {activeTab === 'expenses' && <th className="p-3 border-r-2 border-black">Payment</th>}
                  <th className="p-3 border-r-2 border-black">Amount</th>
                  <th className="p-3 text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredItems.map((item) => (
                  <tr key={item.id} className="border-b-2 border-black text-sm font-bold bg-white hover:bg-[#FFFDE6]">
                    <td className="p-3 border-r-2 border-black text-xs font-bold">{item.expenseDate || item.incomeDate}</td>
                    <td className="p-3 border-r-2 border-black font-black">{item.merchant || item.title || item.source}</td>
                    <td className="p-3 border-r-2 border-black">
                      <span className="nb-badge nb-badge-yellow">{item.category?.name || item.category || 'General'}</span>
                    </td>
                    {activeTab === 'expenses' && (
                      <td className="p-3 border-r-2 border-black">
                        <span className="nb-badge nb-badge-gray">{item.paymentMethod || 'UPI'}</span>
                      </td>
                    )}
                    <td className={`p-3 border-r-2 border-black font-black ${activeTab === 'expenses' ? 'text-[#FF2A85]' : 'text-[#00E5FF]'}`}>
                      {activeTab === 'expenses' ? `-₹${item.amount}` : `+₹${item.amount}`}
                    </td>
                    <td className="p-3 text-center">
                      <button
                        onClick={() => handleDelete(item.id)}
                        className="nb-btn nb-btn-danger p-1.5"
                        title="Delete"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

      </div>

      {/* Add Transaction Modal */}
      {showModal && (
        <div className="nb-modal-overlay">
          <div className="nb-card bg-white max-w-lg w-full p-6 space-y-4">
            <div className="flex items-center justify-between border-b-2 border-black pb-3">
              <h3 className="text-xl font-black uppercase text-black">
                {modalType === 'expense' ? '💸 Add New Expense' : '💰 Add New Income'}
              </h3>
              <button onClick={() => setShowModal(false)} className="nb-btn p-1">
                <X className="w-5 h-5" />
              </button>
            </div>

            {error && (
              <div className="bg-[#FF2A85] text-white border-2 border-black p-3 rounded font-bold text-xs">
                ⚠️ {error}
              </div>
            )}

            <form onSubmit={handleAddSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-black uppercase mb-1">
                  {modalType === 'expense' ? 'Merchant / Title' : 'Income Source'}
                </label>
                <input
                  type="text"
                  required
                  className="nb-input"
                  placeholder={modalType === 'expense' ? 'Domino\'s Pizza' : 'Salary / Freelance'}
                  value={modalType === 'expense' ? formData.merchant : formData.source}
                  onChange={(e) =>
                    modalType === 'expense'
                      ? setFormData({ ...formData, merchant: e.target.value, title: e.target.value })
                      : setFormData({ ...formData, source: e.target.value })
                  }
                />
              </div>

              <div>
                <label className="block text-xs font-black uppercase mb-1">Amount (₹)</label>
                <input
                  type="number"
                  step="0.01"
                  required
                  className="nb-input"
                  placeholder="450.00"
                  value={formData.amount}
                  onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                />
              </div>

              <div>
                <label className="block text-xs font-black uppercase mb-1">Date</label>
                <input
                  type="date"
                  required
                  className="nb-input"
                  value={modalType === 'expense' ? formData.expenseDate : formData.incomeDate}
                  onChange={(e) =>
                    modalType === 'expense'
                      ? setFormData({ ...formData, expenseDate: e.target.value })
                      : setFormData({ ...formData, incomeDate: e.target.value })
                  }
                />
              </div>

              {modalType === 'expense' && (
                <div>
                  <label className="block text-xs font-black uppercase mb-1">Payment Method</label>
                  <select
                    className="nb-input font-bold"
                    value={formData.paymentMethod}
                    onChange={(e) => setFormData({ ...formData, paymentMethod: e.target.value })}
                  >
                    <option value="UPI">UPI</option>
                    <option value="CARD">CREDIT / DEBIT CARD</option>
                    <option value="CASH">CASH</option>
                    <option value="BANK_TRANSFER">BANK TRANSFER</option>
                  </select>
                </div>
              )}

              <div>
                <label className="block text-xs font-black uppercase mb-1">Select Category</label>
                <select
                  className="nb-input font-bold bg-[#FFE600] text-black border-2 border-black"
                  value={formData.categoryId}
                  onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                  required
                >
                  {(availableCategories.length > 0 ? availableCategories : categories).map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.icon || '📁'} {c.name} ({c.type})
                    </option>
                  ))}
                </select>
              </div>

              <div className="pt-2 flex justify-end gap-3">
                <button type="button" onClick={() => setShowModal(false)} className="nb-btn bg-gray-200">
                  Cancel
                </button>
                <button type="submit" className={`nb-btn ${modalType === 'expense' ? 'nb-btn-danger' : 'nb-btn-success'}`}>
                  Save Transaction
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
