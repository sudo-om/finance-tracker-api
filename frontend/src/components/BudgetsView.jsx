import React, { useEffect, useState } from 'react';
import { PiggyBank, Plus, Trash2, Calendar, AlertTriangle, CheckCircle, XCircle, X } from 'lucide-react';
import { api } from '../api';

export default function BudgetsView() {
  const [budgets, setBudgets] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState('');

  // Form State
  const [formData, setFormData] = useState({
    amount: '',
    categoryId: '',
    period: 'monthly', // 'monthly' | 'weekly' | 'yearly' | 'custom'
    startDate: new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0],
    endDate: new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0).toISOString().split('T')[0],
  });

  useEffect(() => {
    fetchBudgets();
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const res = await api.getCategories();
      const catList = Array.isArray(res) ? res : (res?.data || []);
      setCategories(catList);
      if (catList.length > 0) {
        setFormData((prev) => ({ ...prev, categoryId: prev.categoryId || catList[0].id }));
      }
    } catch (err) {
      console.error('Error fetching categories:', err);
    }
  };

  const openModal = () => {
    setError('');
    if (categories.length > 0) {
      const expenseCats = categories.filter((c) => (c.type || '').toUpperCase() === 'EXPENSE');
      setFormData((prev) => ({
        ...prev,
        categoryId: expenseCats.length > 0 ? expenseCats[0].id : categories[0].id,
      }));
    }
    setShowModal(true);
  };

  const fetchBudgets = async () => {
    setLoading(true);
    try {
      const res = await api.getBudgets();
      const list = Array.isArray(res) ? res : (res?.content || []);
      setBudgets(list);
    } catch (err) {
      console.error('Error fetching budgets:', err);
    } finally {
      setLoading(false);
    }
  };

  const handlePeriodChange = (period) => {
    const now = new Date();
    let start, end;

    if (period === 'monthly') {
      start = new Date(now.getFullYear(), now.getMonth(), 1);
      end = new Date(now.getFullYear(), now.getMonth() + 1, 0);
    } else if (period === 'weekly') {
      const day = now.getDay();
      const diff = now.getDate() - day + (day === 0 ? -6 : 1); // Monday
      start = new Date(now.setDate(diff));
      end = new Date(now.setDate(diff + 6));
    } else if (period === 'yearly') {
      start = new Date(now.getFullYear(), 0, 1);
      end = new Date(now.getFullYear(), 11, 31);
    }

    setFormData({
      ...formData,
      period,
      ...(start ? { startDate: start.toISOString().split('T')[0] } : {}),
      ...(end ? { endDate: end.toISOString().split('T')[0] } : {}),
    });
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this budget constraint?')) return;
    try {
      await api.deleteBudget(id);
      fetchBudgets();
    } catch (err) {
      alert(err.message || 'Failed to delete budget');
    }
  };

  const handleCreateBudget = async (e) => {
    e.preventDefault();
    setError('');

    const targetCategory = categories.find((c) => c.id === formData.categoryId) || categories[0];
    const catId = targetCategory ? targetCategory.id : formData.categoryId;

    if (!catId) {
      setError('Please select a valid category');
      return;
    }

    try {
      await api.createBudget({
        amount: parseFloat(formData.amount),
        categoryId: catId,
        startDate: formData.startDate,
        endDate: formData.endDate,
      });

      setShowModal(false);
      fetchBudgets();
    } catch (err) {
      setError(err.message || 'Failed to create budget');
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'OVER_BUDGET':
        return (
          <span className="nb-badge nb-badge-pink flex items-center gap-1">
            <XCircle className="w-3.5 h-3.5" /> OVER BUDGET
          </span>
        );
      case 'WARNING':
        return (
          <span className="nb-badge nb-badge-yellow flex items-center gap-1">
            <AlertTriangle className="w-3.5 h-3.5 text-black" /> WARNING
          </span>
        );
      default:
        return (
          <span className="nb-badge nb-badge-green flex items-center gap-1">
            <CheckCircle className="w-3.5 h-3.5" /> ON TRACK
          </span>
        );
    }
  };

  const availableCategories = categories.filter((c) => (c.type || '').toUpperCase() === 'EXPENSE');

  return (
    <div className="space-y-6">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black uppercase tracking-tight text-black">Budget Tracker</h1>
          <p className="text-sm font-bold text-gray-700">Set category limits and monitor your spending thresholds</p>
        </div>

        <button onClick={openModal} className="nb-btn nb-btn-primary">
          <Plus className="w-4 h-4 stroke-[3]" /> Set New Budget
        </button>
      </div>

      {/* Budgets Grid */}
      {(!budgets || budgets.length === 0) ? (
        <div className="nb-card text-center py-12 bg-white">
          <PiggyBank className="w-12 h-12 mx-auto text-black mb-3" />
          <h3 className="text-lg font-black uppercase">No Active Budgets Found</h3>
          <p className="text-xs font-bold text-gray-600 max-w-sm mx-auto mt-1 mb-4">
            Create monthly or weekly budget limits for your categories to stay on track.
          </p>
          <button onClick={openModal} className="nb-btn nb-btn-primary">
            + Create Your First Budget
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {budgets.map((b) => {
            const percent = Math.min(b.percentageUsed || 0, 100);
            return (
              <div key={b.id} className="nb-card bg-white space-y-4 relative">
                <div className="flex items-center justify-between border-b-2 border-black pb-3">
                  <h3 className="text-lg font-black uppercase text-black">{b.category}</h3>
                  <button onClick={() => handleDelete(b.id)} className="text-gray-400 hover:text-black p-1">
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>

                <div className="space-y-1">
                  <div className="flex items-baseline justify-between">
                    <span className="text-3xl font-black text-black">₹{b.spent || 0}</span>
                    <span className="text-sm font-bold text-gray-600">Limit: ₹{b.amount}</span>
                  </div>
                  <div className="flex items-center justify-between text-xs font-bold text-gray-700">
                    <span>Remaining: ₹{b.remaining || 0}</span>
                    <span>{b.percentageUsed || 0}% used</span>
                  </div>
                </div>

                {/* Progress Bar */}
                <div className="nb-progress-bg">
                  <div
                    className={`nb-progress-fill ${
                      b.status === 'OVER_BUDGET' ? 'bg-[#FF2A85]' : b.status === 'WARNING' ? 'bg-[#FFE600]' : 'bg-[#00E5FF]'
                    }`}
                    style={{ width: `${percent}%` }}
                  />
                </div>

                {/* Card Footer */}
                <div className="flex items-center justify-between pt-2 border-t-2 border-black">
                  <div className="flex items-center gap-1 text-xs font-bold text-gray-600">
                    <Calendar className="w-3.5 h-3.5" />
                    <span>{b.startDate} to {b.endDate}</span>
                  </div>
                  {getStatusBadge(b.status)}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Set New Budget Modal */}
      {showModal && (
        <div className="nb-modal-overlay">
          <div className="nb-card bg-white max-w-md w-full p-6 space-y-4">
            <div className="flex items-center justify-between border-b-2 border-black pb-3">
              <h3 className="text-xl font-black uppercase text-black">🐷 Set Category Budget</h3>
              <button onClick={() => setShowModal(false)} className="nb-btn p-1">
                <X className="w-5 h-5" />
              </button>
            </div>

            {error && (
              <div className="bg-[#FF2A85] text-white border-2 border-black p-3 rounded font-bold text-xs">
                ⚠️ {error}
              </div>
            )}

            <form onSubmit={handleCreateBudget} className="space-y-4">
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

              <div>
                <label className="block text-xs font-black uppercase mb-1">Budget Limit Amount (₹)</label>
                <input
                  type="number"
                  step="0.01"
                  required
                  className="nb-input"
                  placeholder="5000.00"
                  value={formData.amount}
                  onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                />
              </div>

              {/* Period Switcher */}
              <div>
                <label className="block text-xs font-black uppercase mb-1">Period Shortcut</label>
                <div className="grid grid-cols-3 gap-2">
                  {['monthly', 'weekly', 'yearly'].map((p) => (
                    <button
                      key={p}
                      type="button"
                      onClick={() => handlePeriodChange(p)}
                      className={`nb-btn py-1 text-xs ${formData.period === p ? 'nb-btn-primary' : 'bg-white'}`}
                    >
                      {p}
                    </button>
                  ))}
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-black uppercase mb-1">Start Date</label>
                  <input
                    type="date"
                    required
                    className="nb-input text-xs"
                    value={formData.startDate}
                    onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                  />
                </div>
                <div>
                  <label className="block text-xs font-black uppercase mb-1">End Date</label>
                  <input
                    type="date"
                    required
                    className="nb-input text-xs"
                    value={formData.endDate}
                    onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                  />
                </div>
              </div>

              <div className="pt-2 flex justify-end gap-3">
                <button type="button" onClick={() => setShowModal(false)} className="nb-btn bg-gray-200">
                  Cancel
                </button>
                <button type="submit" className="nb-btn nb-btn-primary">
                  Save Budget
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
